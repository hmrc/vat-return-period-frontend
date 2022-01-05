/*
 * Copyright 2022 HM Revenue & Customs
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

import assets.BaseTestConstants.{agentEmail, errorModel}
import assets.CircumstanceDetailsTestConstants._
import base.BaseSpec
import connectors.httpParsers.ResponseHttpParsers.{HttpGetResult, HttpPutResult}
import mocks.MockHttp
import models.circumstanceInfo.CircumstanceDetails
import models.returnFrequency.{Jan, SubscriptionUpdateResponseModel, UpdateReturnPeriod}

import scala.concurrent.Future
import play.api.test.Helpers.{await, defaultAwaitTimeout}

class VatSubscriptionConnectorSpec extends BaseSpec with MockHttp {

  object TestVatSubscriptionConnector extends VatSubscriptionConnector(
    mockHttp,
    mockAppConfig
  )

  "VatSubscriptionConnector" when {

    "calling the .getCustomerDetailsUrl method" should {

      "format the url correctly" in {
        TestVatSubscriptionConnector.getCustomerDetailsUrl("999999999") should endWith("/vat-subscription/999999999/full-information")
      }
    }

    "calling the .updateReturnPeriodUrl method" should {

      "format the url correctly" in {
        TestVatSubscriptionConnector.updateReturnPeriodUrl("999999999") should endWith("/vat-subscription/999999999/return-period")

      }
    }

    "calling the .getCustomerCircumstanceDetails method" when {

      def result: Future[HttpGetResult[CircumstanceDetails]] = TestVatSubscriptionConnector.getCustomerCircumstanceDetails(vrn)

      "a successful response is returned" should {

        "return a CustomerDetailsModel" in {
          setupMockHttpGet(TestVatSubscriptionConnector.getCustomerDetailsUrl(vrn))(Future.successful(Right(circumstanceDetailsModelMax)))
          await(result) shouldBe Right(circumstanceDetailsModelMax)
        }
      }

      "an unsuccessful response is returned" should {
        "return a Left with an ErrorModel" in {
          setupMockHttpGet(TestVatSubscriptionConnector.getCustomerDetailsUrl(vrn))(Future.successful(Left(errorModel)))
          await(result) shouldBe Left(errorModel)
        }
      }
    }

    "calling the .updateReturnFrequency method" when {

      def result: Future[HttpPutResult[SubscriptionUpdateResponseModel]] =
        TestVatSubscriptionConnector.updateReturnFrequency("999999999", UpdateReturnPeriod(Jan.id, Some(agentEmail)))

      "provided with a correct subscription update model" should {

        "return a SubscriptionUpdateResponseModel" in {
          val response = Right(SubscriptionUpdateResponseModel("Ooooooh, it's good"))
          setupMockHttpPut(s"${mockAppConfig.vatSubscriptionBaseURL}/$vrn/return-period")(Future.successful(response))
          await(result) shouldBe response
        }

      }

      "provided with an error" should {
        "return a Left with an ErrorModel" in {
          setupMockHttpPut(s"${mockAppConfig.vatSubscriptionBaseURL}/$vrn/return-period")(Future.successful(errorModel))
          await(result) shouldBe errorModel
        }
      }
    }
  }
}
