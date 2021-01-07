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

package controllers.returnFrequency

import audit.AuditService
import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.AuthPredicate
import javax.inject.{Inject, Singleton}
import models.auth.User
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.CustomerCircumstanceDetailsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.returnFrequency.{ChangeReturnFrequencyConfirmation => CRFCView}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChangeReturnFrequencyConfirmation @Inject()(val authenticate: AuthPredicate,
                                                  customerCircumstanceDetailsService: CustomerCircumstanceDetailsService,
                                                  val serviceErrorHandler: ServiceErrorHandler,
                                                  val auditService: AuditService,
                                                  implicit val appConfig: AppConfig,
                                                  implicit val ec: ExecutionContext,
                                                  mcc: MessagesControllerComponents,
                                                  changeReturnFrequencyConfirmationView:
                                                  CRFCView) extends FrontendController(mcc) with I18nSupport {

  val show: String => Action[AnyContent] = _ => authenticate.async { implicit user =>
    if (user.isAgent) {
      val email = user.session.get(SessionKeys.verifiedAgentEmail)
      customerCircumstanceDetailsService.getCustomerCircumstanceDetails(user.vrn).map {
        case Right(details) =>
          val entityName = details.customerDetails.clientName
          Ok(changeReturnFrequencyConfirmationView(clientName = entityName, agentEmail = email))
        case Left(_) =>
          Ok(changeReturnFrequencyConfirmationView(agentEmail = email))
      }
    } else {renderView}
  }

  private def renderView(implicit user: User[AnyContent]): Future[Result] =
    customerCircumstanceDetailsService.getCustomerCircumstanceDetails(user.vrn).map {
      case Right(details) =>
        Ok(changeReturnFrequencyConfirmationView(
          contactPref = details.commsPreference,
          emailVerified = details.emailVerified.getOrElse(false)
        ))
      case Left(_) =>
        Ok(changeReturnFrequencyConfirmationView())
    }
}
