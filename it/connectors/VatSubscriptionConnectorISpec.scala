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

import base.BaseISpec
import connectors.httpParsers.ResponseHttpParsers.{HttpGetResult, HttpPutResult}
import models.circumstanceInfo.CircumstanceDetails
import models.errors.{ServerSideError, UnexpectedJsonFormat}
import models.returnFrequency.{Jan, SubscriptionUpdateResponseModel, UpdateReturnPeriod}
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import stubs.VatSubscriptionStub._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class VatSubscriptionConnectorISpec extends BaseISpec {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: VatSubscriptionConnector = new VatSubscriptionConnector(httpClient, appConfig)

  "VatSubscriptionConnector" when {

    "calling .getCustomerDetails" when {

      "response is 200" when {

        "response JSON is valid" should {

          "return a CustomerDetails model" in {

            stubGet(s"/vat-subscription/$vrn/full-information", circumstanceDetailsJsonMax.toString(), OK)

            val result: HttpGetResult[CircumstanceDetails] = await(connector.getCustomerCircumstanceDetails(vrn))
            result shouldBe Right(circumstanceDetailsModelMax)
          }
        }

        "response JSON is invalid" should {

          "return an empty model" in {

            val invalidJson: JsObject = Json.obj("invalid" -> "data")

            stubGet(s"/vat-subscription/$vrn/full-information", invalidJson.toString(), OK)

            val result: HttpGetResult[CircumstanceDetails] = await(connector.getCustomerCircumstanceDetails(vrn))
            result shouldBe Left(UnexpectedJsonFormat)
          }
        }
      }

      "response is not 200" should {

        "return an error model" in {

          stubGet(s"/vat-subscription/$vrn/full-information", "", SERVICE_UNAVAILABLE)

          val result: HttpGetResult[CircumstanceDetails] = await(connector.getCustomerCircumstanceDetails(vrn))
          result shouldBe Left(ServerSideError("503", "Received downstream error when retrieving customer details."))
        }
      }
    }

    "calling .updateReturnFrequency" when {

      val updatedReturnPeriod: UpdateReturnPeriod = UpdateReturnPeriod(Jan.id, None)


      "response is 200" when {

        "response JSON is valid" should {

          "return a formBundleID" in {

            val expectedResponse = SubscriptionUpdateResponseModel("12345")
            val expectedResponseJson = Json.obj("formBundle" -> "12345")

            stubPut(s"/vat-subscription/$vrn/return-period", expectedResponseJson.toString, OK)

            val result: HttpPutResult[SubscriptionUpdateResponseModel] = await(connector.updateReturnFrequency(vrn, updatedReturnPeriod))
            result shouldBe Right(expectedResponse)
          }

        }

        "response JSON is invalid" should {

          "return an error model" in {

            val invalidJson: JsObject = Json.obj("invalid" -> "data")

            stubPut(s"/vat-subscription/$vrn/return-period", invalidJson.toString(), OK)

            val result: HttpPutResult[SubscriptionUpdateResponseModel] = await(connector.updateReturnFrequency(vrn, updatedReturnPeriod))
            result shouldBe Left(UnexpectedJsonFormat)

          }
        }
      }

      "response is not 200" should {

        "return an error model" in {

          stubPut(s"/vat-subscription/$vrn/return-period", "", SERVICE_UNAVAILABLE)

          val result: HttpPutResult[SubscriptionUpdateResponseModel] = await(connector.updateReturnFrequency(vrn, updatedReturnPeriod))
          result shouldBe Left(ServerSideError("503", "Received downstream error when retrieving subscription update response."))
        }
      }
    }
  }
}
