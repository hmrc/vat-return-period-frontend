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

package connectors

import base.BaseISpec
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.contactPreferences.ContactPreference
import models.errors.{ServerSideError, UnexpectedJsonFormat}
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import stubs.ContactPreferencesStub._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class ContactPreferencesConnectorISpec extends BaseISpec {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: ContactPreferenceConnector = new ContactPreferenceConnector(httpClient, appConfig)

  "ContactPreferenceConnector" when {

    "calling .getContactPreference" when {

      "response is 200" when {

        "response JSON is valid" should {

          "return a ContactPreference model" in {

            stubGet(s"/contact-preferences/vat/vrn/$vrn", digitalContactPreferenceJson.toString(), OK)

            val result: HttpGetResult[ContactPreference] = await(connector.getContactPreference(vrn))
            result shouldBe Right(digitalContactPreferenceModel)
          }
        }

        "response JSON is invalid" should {

          "return an error model" in {

            val invalidJson: JsObject = Json.obj("invalid" -> "data")

            stubGet(s"/contact-preferences/vat/vrn/$vrn", invalidJson.toString(), OK)

            val result: HttpGetResult[ContactPreference] = await(connector.getContactPreference(vrn))
            result shouldBe Left(UnexpectedJsonFormat)
          }
        }
      }

      "response is not 200" should {

        "return an error model" in {

          stubGet(s"/contact-preferences/vat/vrn/$vrn", "", SERVICE_UNAVAILABLE)

          val result: HttpGetResult[ContactPreference] = await(connector.getContactPreference(vrn))
          result shouldBe Left(ServerSideError("503", "Received downstream error when retrieving contact preferences."))
        }
      }
    }
  }
}
