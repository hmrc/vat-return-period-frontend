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

package connectors.httpParsers

import base.BaseSpec
import connectors.httpParsers.SubscriptionUpdateHttpParser.SubscriptionUpdateReads
import models.errors.{ServerSideError, UnexpectedJsonFormat}
import models.returnFrequency.SubscriptionUpdateResponseModel
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

class SubscriptionUpdateHttpParserSpec extends BaseSpec {

  "SubscriptionUpdateHttpParser" when {

    "http response status is OK with valid json" should {

      val successJson = Json.obj("formBundle" -> "12345")
      val result = SubscriptionUpdateReads.read("", "", HttpResponse.apply(Status.OK, successJson, Map.empty[String, Seq[String]]))

      "return SubscriptionUpdateResponseModel" in {
        result shouldBe Right(SubscriptionUpdateResponseModel("12345"))
      }
    }

    "http response status is OK with invalid json" should {

      val invalidJson = Json.obj("invalidKey" -> "12345")
      val result = SubscriptionUpdateReads.read("", "", HttpResponse.apply(Status.OK, invalidJson, Map.empty[String, Seq[String]]))

      "return ErrorModel" in {
        result shouldBe Left(UnexpectedJsonFormat)
      }
    }

    "http response status is not OK" should {

      val result = SubscriptionUpdateReads.read("", "", HttpResponse.apply(Status.INTERNAL_SERVER_ERROR, "", Map.empty[String, Seq[String]]))

      "return ErrorModel" in {
        result shouldBe Left(ServerSideError("500", "Received downstream error when retrieving subscription update response."))
      }
    }
  }
}
