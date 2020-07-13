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

package connectors.httpParsers

import base.BaseSpec
import connectors.httpParsers.ContactPreferenceHttpParser.ContactPreferenceReads
import models.contactPreferences.ContactPreference
import models.errors.{ServerSideError, UnexpectedJsonFormat}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

class ContactPreferenceHttpParserSpec extends BaseSpec {

  "The ContactPreferenceHttpParser" when {

    "the http response status is OK" when {

      "preference is Digital" should {

        val response = Json.obj("preference" -> "DIGITAL")

        "return Digital" in {
          ContactPreferenceReads.read("", "", HttpResponse.apply(Status.OK, response, Map.empty[String, Seq[String]])) shouldBe
            Right(ContactPreference("DIGITAL"))
        }
      }

      "preference is Paper" should {

        val response = Json.obj("preference" -> "PAPER")

        "return Paper" in {
          ContactPreferenceReads.read("", "", HttpResponse.apply(Status.OK, response, Map.empty[String, Seq[String]])) shouldBe
            Right(ContactPreference("PAPER"))
        }
      }

      "preference is of various cases" should {

        val response = Json.obj("preference" -> "digITaL")

        "return DIGITAL" in {
          ContactPreferenceReads.read("", "", HttpResponse.apply(Status.OK, response, Map.empty[String, Seq[String]])) shouldBe
            Right(ContactPreference("DIGITAL"))
        }

      }

      "preference is invalid" should {

        val response = Json.obj("preference" -> "Invalid")

        "return an ErrorModel" in {
          ContactPreferenceReads.read("", "", HttpResponse.apply(Status.OK, response, Map.empty[String, Seq[String]])) shouldBe
            Left(UnexpectedJsonFormat)
        }
      }

      "json is invalid" should {

        val response = Json.obj("Invalid" -> "Invalid")

        "return an ErrorModel" in {
          ContactPreferenceReads.read("", "", HttpResponse.apply(Status.OK, response, Map.empty[String, Seq[String]])) shouldBe
            Left(UnexpectedJsonFormat)
        }
      }
    }

    "the http response status is unexpected" should {

      "return an ErrorModel" in {
        ContactPreferenceReads.read("", "", HttpResponse.apply(Status.NOT_FOUND, "Response body", Map.empty[String, Seq[String]])) shouldBe
          Left(ServerSideError(Status.NOT_FOUND.toString,"Received downstream error when retrieving contact preferences."))
      }
    }
  }
}
