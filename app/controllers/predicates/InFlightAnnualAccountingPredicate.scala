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

import common.SessionKeys.mtdVatvcCurrentAnnualAccounting
import config.{AppConfig, ServiceErrorHandler}
import javax.inject.Inject
import models.auth.User
import models.circumstanceInfo.ChangeIndicators
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.{Ok, Redirect}
import play.api.mvc.{ActionRefiner, Result}
import services.CustomerCircumstanceDetailsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.LoggerUtil
import views.html.annualAccounting.PreventLeaveAnnualAccounting

import scala.concurrent.{ExecutionContext, Future}

class InFlightAnnualAccountingPredicate @Inject()(customerCircumstancesService: CustomerCircumstanceDetailsService,
                                                  val serviceErrorHandler: ServiceErrorHandler,
                                                  implicit val appConfig: AppConfig,
                                                  implicit val executionContext: ExecutionContext,
                                                  implicit val messagesApi: MessagesApi,
                                                  preventLeaveAnnualAccountingView: PreventLeaveAnnualAccounting)
  extends ActionRefiner[User, User] with I18nSupport with LoggerUtil {

  override def refine[A](request: User[A]): Future[Either[Result, User[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    implicit val user: User[A] = request

    user.session.get(mtdVatvcCurrentAnnualAccounting) match {
      case Some("true") => Future.successful(Left(Ok(preventLeaveAnnualAccountingView())))
      case Some("false") => Future.successful(Right(user))
      case _ => getCustomerCircumstanceDetails
    }
  }

  private def getCustomerCircumstanceDetails[A](implicit user: User[A], hc: HeaderCarrier): Future[Either[Result, User[A]]] = {

    customerCircumstancesService.getCustomerCircumstanceDetails(user.vrn).map {

      case Right(circumstanceDetails) =>
        if (getAnnualAccounting(circumstanceDetails.changeIndicators)) {
          Left(Ok(preventLeaveAnnualAccountingView())
            .addingToSession(mtdVatvcCurrentAnnualAccounting -> "true"))
        } else {
          Left(Redirect(controllers.returnFrequency.routes.ChooseDatesController.show().url)
            .addingToSession(mtdVatvcCurrentAnnualAccounting -> "false"))
        }
      case Left(error) =>
        logger.warn(s"[InFlightAnnualAccountingPredicate][refine] - The call to the GetCustomerInfo API failed. Error: ${error.message}")
        Left(serviceErrorHandler.showInternalServerError)
    }
  }

  private def getAnnualAccounting(changeIndicator: Option[ChangeIndicators]): Boolean = {
    changeIndicator match {
      case Some(changeIndicator) => changeIndicator.annualAccounting
      case _ => logger.info("[InFlightAnnualAccountingPredicate][refine] - No changeIndicators returned from GetCustomerInfo ")
        false
    }
  }
}

