/*
 * Copyright 2019 HM Revenue & Customs
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

import common.SessionKeys.ANNUAL_ACCOUNTING_BOOLEAN
import config.{AppConfig, ServiceErrorHandler}
import javax.inject.Inject
import models.auth.User
import models.circumstanceInfo.ChangeIndicators
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import services.CustomerCircumstanceDetailsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class InFlightAnnualAccountingPredicate @Inject()(customerCircumstancesService: CustomerCircumstanceDetailsService,
                                                  val serviceErrorHandler: ServiceErrorHandler,
                                                  val messagesApi: MessagesApi,
                                                  implicit val appConfig: AppConfig,
                                                  implicit val ec: ExecutionContext)
  extends ActionRefiner[User, User] with I18nSupport {

  override def refine[A](request: User[A]): Future[Either[Result, User[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    implicit val user: User[A] = request

    user.session.get(ANNUAL_ACCOUNTING_BOOLEAN) match {
      case Some("true") => Future.successful(Left(Redirect(controllers.annualAccounting.routes.PreventLeaveAnnualAccountingController.show().url)))
      case Some("false") => Future.successful(Right(user))
      case _ => getCustomerCircumstanceDetails
      case None => getCustomerCircumstanceDetails
    }
  }

  private def getCustomerCircumstanceDetails[A](implicit user: User[A], hc: HeaderCarrier): Future[Either[Result, User[A]]] = {

    customerCircumstancesService.getCustomerCircumstanceDetails(user.vrn).map {

      case Right(circumstanceDetails) =>
        getAnnualAccounting(circumstanceDetails.changeIndicators) match {
          case true =>
            Left(Redirect(controllers.annualAccounting.routes.PreventLeaveAnnualAccountingController.show().url)
              .addingToSession(ANNUAL_ACCOUNTING_BOOLEAN -> "true"))

          case false =>
            Left(Redirect(controllers.returnFrequency.routes.ChooseDatesController.show().url)
              .addingToSession(ANNUAL_ACCOUNTING_BOOLEAN -> "false"))
        }
      case Left(error) =>
        Logger.warn(s"[InFlightAnnualAccountingPredicate][refine] - The call to the GetCustomerInfo API failed. Error: ${error.message}")
        Left(serviceErrorHandler.showInternalServerError)
    }
  }

  private def getAnnualAccounting(changeIndicator: Option[ChangeIndicators]): Boolean = {
    changeIndicator match {
      case Some(changeIndicator) => changeIndicator.annualAccounting
      case _ => Logger.warn("[InFlightAnnualAccountingPredicate][refine] - No changeIndicators returned from GetCustomerInfo ")
        false
    }
  }
}

