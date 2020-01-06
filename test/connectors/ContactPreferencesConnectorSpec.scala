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

package connectors

import assets.BaseTestConstants.errorModel
import assets.ContactPreferenceTestConstants.digitalContactPreferenceModel
import base.BaseSpec
import config.FrontendAppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import mocks.MockHttp
import models.contactPreferences.ContactPreference
import play.api.{Configuration, Environment}

import scala.concurrent.Future

class ContactPreferencesConnectorSpec extends BaseSpec with MockHttp {

  val env: Environment = Environment.simple()
  val configuration: Configuration = Configuration.load(env)

  object TestContactPreferencesConnector extends ContactPreferenceConnector(
    mockHttp,
    new FrontendAppConfig(env, configuration)
  )

  "ContactPreferenceConnector" when {

    "calling the .getContactPreferencesUrl method" should {
      "format the url correctly" in {
        TestContactPreferencesConnector.getContactPreferencesUrl("999999999") should endWith("/contact-preferences/vat/vrn/999999999")
      }
    }

    "calling the .getContactPreference method" when {

      def result: Future[HttpGetResult[ContactPreference]] = TestContactPreferencesConnector.getContactPreference(vrn)

      "a successful response is returned" should {

        "return a ContactPreference model" in {
          setupMockHttpGet(TestContactPreferencesConnector.getContactPreferencesUrl(vrn))(Right(digitalContactPreferenceModel))
          await(result) shouldBe Right(digitalContactPreferenceModel)
        }
      }

      "an unsuccessful response is returned" should {
        "return a Left with an ErrorModel" in {
          setupMockHttpGet(TestContactPreferencesConnector.getContactPreferencesUrl(vrn))(Left(errorModel))
          await(result) shouldBe Left(errorModel)
        }
      }
    }

  }

}
