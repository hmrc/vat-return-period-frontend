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

package mocks.connectors

import connectors.VatSubscriptionConnector
import connectors.httpParsers.ResponseHttpParsers.{HttpGetResult, HttpPutResult}
import models.circumstanceInfo.CircumstanceDetails
import models.returnFrequency.{SubscriptionUpdateResponseModel, UpdateReturnPeriod}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

trait MockSubscriptionConnector extends UnitSpec with MockFactory {

  val mockSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]

  def setupMockUserDetails(vrn: String)(response: HttpGetResult[CircumstanceDetails]): Unit = {
    (mockSubscriptionConnector.getCustomerCircumstanceDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(vrn, *, *)
      .returns(Future.successful(response))
  }

  def setupMockUpdateReturnFrequency(response: HttpPutResult[SubscriptionUpdateResponseModel]): Unit = {
    (mockSubscriptionConnector.updateReturnFrequency(_: String, _: UpdateReturnPeriod)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(Future.successful(response))
  }
}

