/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.predicates

import common.{EnrolmentKeys, SessionKeys}
import common.EnrolmentKeys._
import common.SessionKeys._
import config.{AppConfig, ServiceErrorHandler}
import javax.inject.{Inject, Singleton}
import models.auth.User
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{CustomerCircumstanceDetailsService, EnrolmentsAuthService}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.{UnauthorisedAgent, UnauthorisedNonAgent}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthPredicate @Inject()(authService: EnrolmentsAuthService,
                              errorHandler: ServiceErrorHandler,
                              customerCircumstanceDetailsService: CustomerCircumstanceDetailsService,
                              implicit val appConfig: AppConfig,
                              val mcc: MessagesControllerComponents,
                              unauthorisedAgentView: UnauthorisedAgent,
                              unauthorisedNonAgentView: UnauthorisedNonAgent) extends FrontendController(mcc)
  with I18nSupport
  with ActionBuilder[User, AnyContent]
  with ActionFunction[Request, User]
  with LoggerUtil {

  override val parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
  implicit val executionContext: ExecutionContext = mcc.executionContext

  override def invokeBlock[A](request: Request[A], block: User[A] => Future[Result]): Future[Result] = {

    implicit val req: Request[A] = request

    authService
      .authorised()
      .retrieve(affinityGroup and allEnrolments) {
        case Some(Agent) ~ enrolments =>
          if (enrolments.enrolments.exists(_.key == EnrolmentKeys.agentEnrolmentId)) {
            authoriseAsAgent(block)
          } else {
            logger.debug("[AuthPredicate][invokeBlock] - Agent does not have correct agent enrolment ID")
            Future.successful(Forbidden(unauthorisedAgentView()))
          }
        case Some(_) ~ enrolments => authoriseAsNonAgent(enrolments, block)
        case None ~ _ =>
          logger.warn("[AuthPredicate][invokeBlock] - Missing affinity group")
          Future.successful(errorHandler.showInternalServerError)
      } recover {
      case _: NoActiveSession =>
        logger.debug(s"[AuthPredicate][invokeBlock] - No active session. Redirecting to ${appConfig.signInUrl}")
        Redirect(appConfig.signInUrl)
      case _: AuthorisationException =>
        logger.warn("[AuthPredicate][invokeBlock] - Unauthorised exception when retrieving affinity and all enrolments")
        errorHandler.showInternalServerError
    }
  }

  private def authoriseAsNonAgent[A](enrolments: Enrolments, block: User[A] => Future[Result])
                                    (implicit request: Request[A]): Future[Result] = {
    enrolments.enrolments.collectFirst {
      case Enrolment(EnrolmentKeys.vatEnrolmentId, Seq(EnrolmentIdentifier(_, vrn)), EnrolmentKeys.activated, _) => vrn
    } match {
      case Some(vrn) =>
        val user = User(vrn)
        request.session.get(SessionKeys.insolventWithoutAccessKey) match {
          case Some("true") => Future.successful(Forbidden(unauthorisedNonAgentView()))
          case Some("false") => block(user)
          case _ => customerCircumstanceDetailsService.getCustomerCircumstanceDetails(user.vrn).flatMap {
            case Right(details) if details.customerDetails.isInsolventWithoutAccess =>
              logger.debug("[AuthPredicate][authoriseAsNonAgent] - User is insolvent and not continuing to trade")
              Future.successful(Forbidden(unauthorisedNonAgentView()).addingToSession(SessionKeys.insolventWithoutAccessKey -> "true"))
            case Right(_) =>
              logger.debug("[AuthPredicate][authoriseAsNonAgent] - Authenticated as principle")
              block(user).map(result => result.addingToSession(SessionKeys.insolventWithoutAccessKey -> "false"))
            case _ =>
              logger.warn("[AuthPredicate][authoriseAsNonAgent] - Failure obtaining insolvency status from Customer Info API")
              Future.successful(errorHandler.showInternalServerError)
          }
        }
      case None =>
        logger.debug("[AuthPredicate][authoriseAsNonAgent] - Non-agent with no HMRC-MTD-VAT enrolment. Rendering unauthorised view.")
        Future.successful(Forbidden(unauthorisedNonAgentView()))
    }
  }

  private def authoriseAsAgent[A](block: User[A] => Future[Result])
                                 (implicit request: Request[A]): Future[Result] = {

    val agentDelegatedAuthorityRule: String => Enrolment = vrn =>
      Enrolment(vatEnrolmentId)
        .withIdentifier(vatIdentifierId, vrn)
        .withDelegatedAuthRule(mtdVatDelegatedAuthRule)

    request.session.get(CLIENT_VRN) match {
      case Some(vrn) =>
        authService
          .authorised(agentDelegatedAuthorityRule(vrn))
          .retrieve(allEnrolments) {
            enrolments =>
              enrolments.enrolments.collectFirst {
                case Enrolment(EnrolmentKeys.agentEnrolmentId, Seq(EnrolmentIdentifier(_, arn)), EnrolmentKeys.activated, _) => arn
              } match {
                case Some(arn) => block(User(vrn, active = true, Some(arn)))
                case None =>
                  logger.debug("[AuthPredicate][authoriseAsAgent] - Agent with no HMRC-AS-AGENT enrolment. Rendering unauthorised view.")
                  Future.successful(Forbidden(unauthorisedAgentView()))
              }
          } recover {
          case _: NoActiveSession =>
            logger.debug(s"AuthoriseAsAgentWithClient][authoriseAsAgent] - No active session. Redirecting to ${appConfig.signInUrl}")
            Redirect(appConfig.signInUrl)
          case _: AuthorisationException =>
            logger.debug(s"[AuthoriseAsAgentWithClient][authoriseAsAgent] - Agent does not have delegated authority for Client. " +
              s"Redirecting to ${appConfig.agentClientUnauthorisedUrl(request.uri)}")
            Redirect(appConfig.agentClientUnauthorisedUrl(request.uri))
        }

      case None =>
        logger.debug(s"[AuthPredicate][authoriseAsAgent] - No Client VRN in session. Redirecting to ${appConfig.agentClientLookupStartUrl}")
        Future.successful(Redirect(appConfig.agentClientLookupStartUrl(controllers.returnFrequency.routes.ChooseDatesController.show().url)))
    }
  }
}
