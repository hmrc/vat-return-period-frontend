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

package connectors

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import connectors.httpParsers.CircumstanceDetailsHttpParser.CircumstanceDetailsReads
import connectors.httpParsers.ResponseHttpParsers.{HttpGetResult, HttpPutResult}
import models.circumstanceInfo.CircumstanceDetails
import models.returnFrequency.{SubscriptionUpdateResponseModel, UpdateReturnPeriod}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatSubscriptionConnector @Inject()(val http: HttpClient,
                                         val config: AppConfig) {

  private[connectors] def getCustomerDetailsUrl(vrn: String) = config.vatSubscriptionBaseURL + s"/vat-subscription/$vrn/full-information"

  private[connectors] def updateReturnPeriodUrl(vrn: String) = config.vatSubscriptionBaseURL + s"/vat-subscription/$vrn/return-period"

  def getCustomerCircumstanceDetails(id: String)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CircumstanceDetails]] = {
    val url = getCustomerDetailsUrl(id)
    Logger.debug(s"[CustomerDetailsConnector][getCustomerDetails]: Calling getCustomerDetails with URL - $url")
    http.GET(url)(CircumstanceDetailsReads, headerCarrier, ec)
  }

  def updateReturnFrequency(vrn: String, frequency: UpdateReturnPeriod)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpPutResult[SubscriptionUpdateResponseModel]] = {

    import connectors.httpParsers.SubscriptionUpdateHttpParser.SubscriptionUpdateReads

    val url = updateReturnPeriodUrl(vrn)
    http.PUT[UpdateReturnPeriod, HttpPutResult[SubscriptionUpdateResponseModel]](url, frequency)
  }

}
