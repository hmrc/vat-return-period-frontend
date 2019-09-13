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

package connectors.httpParsers

import assets.CircumstanceDetailsTestConstants._
import base.BaseSpec
import connectors.httpParsers.CircumstanceDetailsHttpParser.CircumstanceDetailsReads
import models.errors.{ServerSideError, UnexpectedJsonFormat}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

class CircumstanceDetailsHttpParserSpec extends BaseSpec {

  val successBadJson = Some(Json.obj("firstName" -> 1))

  "The CustomerDetailsHttpParser" when {

    "the http response status is OK and with valid Json" should {

      "return a CustomerDetailsModel" in {
        CircumstanceDetailsReads.read("", "", HttpResponse(Status.OK, Some(circumstanceDetailsJsonMax))) shouldBe Right(circumstanceDetailsModelMax)
      }
    }

    "the http response status is OK with invalid Json" should {

      "return an ErrorModel" in {
        CircumstanceDetailsReads.read("", "", HttpResponse(Status.OK, successBadJson)) shouldBe
          Left(UnexpectedJsonFormat)
      }
    }

    "the http response status is BAD_REQUEST" should {

      "return an ErrorModel" in {
        CircumstanceDetailsReads.read("", "", HttpResponse(Status.BAD_REQUEST, None)) shouldBe
          Left(ServerSideError(Status.BAD_REQUEST.toString,"Received downstream error when retrieving customer details."))
      }
    }

    "the http response status unexpected" should {

      "return an ErrorModel" in {
        CircumstanceDetailsReads.read("", "", HttpResponse(Status.SEE_OTHER, None)) shouldBe
          Left(ServerSideError(Status.SEE_OTHER.toString,"Received downstream error when retrieving customer details."))
      }
    }
  }
}
