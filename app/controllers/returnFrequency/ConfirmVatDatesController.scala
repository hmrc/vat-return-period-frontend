/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.returnFrequency

import audit.AuditService
import audit.models.UpdateReturnFrequencyAuditModel
import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, InFlightAnnualAccountingPredicate, InFlightReturnFrequencyPredicate}

import javax.inject.{Inject, Singleton}
import models.auth.User
import models.errors.ServerSideError
import models.returnFrequency._
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{CustomerCircumstanceDetailsService, ReturnFrequencyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.returnFrequency.ConfirmDates
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmVatDatesController @Inject()(authenticate: AuthPredicate,
                                          serviceErrorHandler: ServiceErrorHandler,
                                          returnFrequencyService: ReturnFrequencyService,
                                          customerCircumstanceDetailsService: CustomerCircumstanceDetailsService,
                                          auditService: AuditService,
                                          pendingReturnFrequency: InFlightReturnFrequencyPredicate,
                                          pendingAnnualAccountChange: InFlightAnnualAccountingPredicate,
                                          mcc: MessagesControllerComponents,
                                          confirmDates: ConfirmDates)
                                         (implicit appConfig: AppConfig,
                                          ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  val show: Action[AnyContent] = (authenticate andThen
                                  pendingReturnFrequency andThen
                                  pendingAnnualAccountChange) { implicit user =>
    (user.session.get(SessionKeys.mtdVatvcNewReturnFrequency), user.session.get(SessionKeys.mtdVatvcCurrentReturnFrequency)) match {
      case (Some(newFrequency), Some(currentFrequency)) =>
        (ReturnPeriod(newFrequency), ReturnPeriod(currentFrequency)) match {
          case (Some(nf), Some(cf)) =>
            Ok(confirmDates(nf, cf == Annually))
          case _ => serviceErrorHandler.showInternalServerError
        }
      case _ =>
        logger.info("[ConfirmVatDatesController][show] No mtdVatvcNewReturnFrequency found in session. " +
          "Redirecting to Choose Dates page")
        Redirect(controllers.returnFrequency.routes.ChooseDatesController.show.url)
    }
  }

  val submit: Action[AnyContent] = authenticate.async { implicit user =>

    (user.session.get(SessionKeys.mtdVatvcCurrentReturnFrequency), user.session.get(SessionKeys.mtdVatvcNewReturnFrequency)) match {
      case (Some(currentFrequency), Some(newFrequency)) =>
        updateReturnFrequency(ReturnPeriod(currentFrequency), ReturnPeriod(newFrequency))
      case (_, _) =>
        logger.info("[ConfirmVatDatesController][submit] No mtdVatvcNewReturnFrequency and/or " +
          "mtdVatvcCurrentReturnFrequency found in session. Redirecting to Choose Dates page")
        Future.successful(Redirect(controllers.returnFrequency.routes.ChooseDatesController.show.url))
    }
  }

  private def updateReturnFrequency(currentReturnPeriod: Option[ReturnPeriod],
                                    newReturnPeriod: Option[ReturnPeriod])
                                   (implicit user: User[AnyContent]): Future[Result] = {
    (currentReturnPeriod, newReturnPeriod) match {
      case (Some(currentPeriod), Some(newPeriod)) =>
        customerCircumstanceDetailsService.getCustomerCircumstanceDetails(user.vrn).flatMap {
          case Right(details) =>
            returnFrequencyService.updateReturnFrequency(user.vrn, newPeriod).map {
              case Right(_) =>
                auditService.extendedAudit(
                  UpdateReturnFrequencyAuditModel(user, currentPeriod, newPeriod, details.partyType),
                  Some(routes.ConfirmationController.show.url)
                )
                Redirect(
                  controllers.returnFrequency.routes.ConfirmationController.show
                ).removingFromSession(SessionKeys.mtdVatvcNewReturnFrequency, SessionKeys.mtdVatvcCurrentReturnFrequency)
              case Left(ServerSideError("409", _)) =>
                logger.warn("[ConfirmVatDatesController][updateReturnFrequency] Stagger update already in progress. " +
                  "Redirecting user to manage VAT overview")
                Redirect(appConfig.manageVatUrl)
              case _ =>
                serviceErrorHandler.showInternalServerError
            }
          case _ =>
            Future.successful(serviceErrorHandler.showInternalServerError)
        }
      case _ =>
        logger.warn("[ConfirmVatDatesController][updateReturnFrequency] " +
          "mtdVatvcNewReturnFrequency and/or mtdVatvcCurrentReturnFrequency session keys are not valid")
        Future.successful(serviceErrorHandler.showInternalServerError)
    }
  }
}
