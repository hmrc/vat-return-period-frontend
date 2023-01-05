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

import common.SessionKeys
import config.AppConfig
import controllers.predicates.AuthPredicate
import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.CustomerCircumstanceDetailsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.returnFrequency.ChangeReturnFrequencyConfirmation

import scala.concurrent.ExecutionContext

@Singleton
class ConfirmationController @Inject()(authenticate: AuthPredicate,
                                       customerCircumstanceDetailsService: CustomerCircumstanceDetailsService,
                                       confirmationView: ChangeReturnFrequencyConfirmation,
                                       mcc: MessagesControllerComponents)
                                      (implicit appConfig: AppConfig,
                                       ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  val show: Action[AnyContent] = authenticate.async { implicit user =>
    val agentEmailFromSession = user.session.get(SessionKeys.mtdVatvcVerifiedAgentEmail)
    customerCircumstanceDetailsService.getCustomerCircumstanceDetails(user.vrn).map {
      case Right(details) =>
        Ok(confirmationView(
          agentEmail = agentEmailFromSession,
          clientName = details.customerDetails.clientName,
          contactPref = details.commsPreference,
          emailVerified = details.emailVerified.getOrElse(false)
        ))
      case Left(_) =>
        Ok(confirmationView(agentEmail = agentEmailFromSession))
    }
  }
}
