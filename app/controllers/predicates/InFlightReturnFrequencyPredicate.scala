/*
 * Copyright 2024 HM Revenue & Customs
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

import common.SessionKeys.mtdVatvcCurrentReturnFrequency
import config.{AppConfig, ServiceErrorHandler}
import javax.inject.Inject
import models.auth.User
import models.circumstanceInfo.ChangeIndicators
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, MessagesControllerComponents, Result}
import services.CustomerCircumstanceDetailsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class InFlightReturnFrequencyPredicate @Inject()(customerCircumstancesService: CustomerCircumstanceDetailsService,
                                                 val serviceErrorHandler: ServiceErrorHandler,
                                                 val messagesApi: MessagesApi,
                                                 implicit val appConfig: AppConfig,
                                                 val mcc: MessagesControllerComponents)
  extends ActionRefiner[User, User] with I18nSupport with LoggerUtil {

  implicit val executionContext: ExecutionContext = mcc.executionContext

  override def refine[A](request: User[A]): Future[Either[Result, User[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    implicit val user: User[A] = request

    user.session.get(mtdVatvcCurrentReturnFrequency) match {
      case Some(_) => Future.successful(Right(user))
      case None => getCustomerCircumstanceDetails
    }
  }

  private def getCustomerCircumstanceDetails[A](implicit user: User[A], hc: HeaderCarrier): Future[Either[Result, User[A]]] = {
    customerCircumstancesService.getCustomerCircumstanceDetails(user.vrn).map {

      case Right(circumstanceDetails) if getReturnPeriod(circumstanceDetails.changeIndicators) =>
        Left(Redirect(appConfig.manageVatUrl))
      case Right(circumstanceDetails) =>
        circumstanceDetails.returnPeriod match {
          case Some(returnPeriod) =>
            Left(Redirect(controllers.returnFrequency.routes.ChooseDatesController.show.url)
              .addingToSession(mtdVatvcCurrentReturnFrequency -> returnPeriod.id))
          case None =>
            logger.warn("[InFlightReturnFrequencyPredicate][refine] - No return frequency returned from GetCustomerInfo")
            Left(Redirect(appConfig.manageVatUrl))
        }
      case Left(error) =>
        logger.warn(s"[InFlightReturnFrequencyPredicate][refine] - The call to the GetCustomerInfo API failed. Error: ${error.message}")
        Left(serviceErrorHandler.showInternalServerError)
    }
  }

  private def getReturnPeriod(changeIndicator: Option[ChangeIndicators]): Boolean = {
    changeIndicator match {
      case Some(indicators) => indicators.returnPeriod.getOrElse(false)
      case _ => false
    }
  }
}

