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

import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import controllers.predicates.{AuthPredicate, InFlightAnnualAccountingPredicate, InFlightReturnFrequencyPredicate}
import forms.ChooseDatesForm.datesForm
import javax.inject.{Inject, Singleton}
import models.returnFrequency.{ReturnDatesModel, ReturnPeriod}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CustomerCircumstanceDetailsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.returnFrequency.ChooseDates

@Singleton
class ChooseDatesController @Inject()(val authenticate: AuthPredicate,
                                      val pendingReturnFrequency: InFlightReturnFrequencyPredicate,
                                      val pendingAnnualAccountChange: InFlightAnnualAccountingPredicate,
                                      val customerCircumstanceDetailsService: CustomerCircumstanceDetailsService,
                                      val serviceErrorHandler: ServiceErrorHandler,
                                      val mcc: MessagesControllerComponents,
                                      implicit val appConfig: AppConfig,
                                      chooseDatesView: ChooseDates) extends FrontendController(mcc) with I18nSupport {


  val show: Action[AnyContent] = (authenticate andThen pendingReturnFrequency andThen pendingAnnualAccountChange) { implicit user =>

    val currentReturnFrequency: String = user.session.get(SessionKeys.CURRENT_RETURN_FREQUENCY).get
    val form: Form[ReturnDatesModel] = user.session.get(SessionKeys.NEW_RETURN_FREQUENCY) match {
      case Some(value) => datesForm.fill(ReturnDatesModel(value))
      case _ => datesForm
    }

    ReturnPeriod(currentReturnFrequency).fold(serviceErrorHandler.showInternalServerError) { returnFrequency =>
      Ok(chooseDatesView(form, returnFrequency))
    }
  }

  val submit: Action[AnyContent] = (authenticate andThen pendingReturnFrequency andThen pendingAnnualAccountChange) { implicit user =>

    val currentReturnFrequency: String = user.session.get(SessionKeys.CURRENT_RETURN_FREQUENCY).get
    datesForm.bindFromRequest().fold(
      errors =>
        ReturnPeriod(currentReturnFrequency).fold(serviceErrorHandler.showInternalServerError)(returnFrequency =>
          BadRequest(chooseDatesView(errors, returnFrequency))
        ),
      success =>
        Redirect(controllers.returnFrequency.routes.ConfirmVatDatesController.show()).addingToSession(SessionKeys.NEW_RETURN_FREQUENCY -> success.current)
    )
  }
}
