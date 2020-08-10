/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers

import common.SessionKeys
import config.AppConfig
import controllers.predicates.AuthPredicate
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future

class ChangeClientController @Inject()(val authenticate: AuthPredicate,
                                       implicit val appConfig: AppConfig,
                                       val mcc: MessagesControllerComponents) extends FrontendController(mcc) {

  def changeClient: Action[AnyContent] = authenticate.async {
    implicit user =>
      Future.successful(
        Redirect(appConfig.agentClientLookupStartUrl(appConfig.manageVatUrl)).removingFromSession(SessionKeys.CLIENT_VRN,
          SessionKeys.NEW_RETURN_FREQUENCY, SessionKeys.CURRENT_RETURN_FREQUENCY, SessionKeys.ANNUAL_ACCOUNTING_PENDING)
      )
  }
}
