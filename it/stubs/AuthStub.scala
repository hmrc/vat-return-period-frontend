/*
 * Copyright 2023 HM Revenue & Customs
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

package stubs

import base.BaseISpec
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.OK
import play.api.libs.json.{JsObject, Json}
import utils.WireMockMethods

object AuthStub extends BaseISpec with WireMockMethods {

  val uri: String = "/auth/authorise"

  val mtdVatAuthResponse: JsObject = Json.obj(
    "affinityGroup" -> "Individual",
    "allEnrolments" -> Json.arr(
      Json.obj(
        "key" -> "HMRC-MTD-VAT",
        "identifiers" -> Json.arr(
          Json.obj(
            "key" -> "VRN",
            "value" -> "999999999"
          )
        )
      )
    )
  )

  val otherEnrolmentAuthResponse: JsObject = Json.obj(
    "affinityGroup" -> "Individual",
    "allEnrolments" -> Json.arr(
      Json.obj(
        "key" -> "OTHER",
        "identifiers" -> Json.arr(
          Json.obj(
            "key" -> "OTHER",
            "value" -> "12345"
          )
        )
      )
    )
  )

  val agentEnrolmentAuthResponse: JsObject = Json.obj(
    "affinityGroup" -> "Agent",
    "allEnrolments" -> Json.arr(
      Json.obj(
        "key" -> "HMRC-AS-AGENT",
        "identifiers" -> Json.arr(
          Json.obj(
            "key" -> "AgentReferenceNumber",
            "value" -> "XARN1234567"
          )
        )
      )
    )
  )

  val agentUnauthorisedAuthResponse: JsObject = Json.obj(
    "affinityGroup" -> "Agent",
    "allEnrolments" -> Json.arr(
      Json.obj(
        "key" -> "OTHER",
        "identifiers" -> Json.arr(
          Json.obj(
            "key" -> "OTHER",
            "value" -> "1234567"
          )
        )
      )
    )
  )

  def authorised(): StubMapping = {
    when(method = POST, uri = uri)
      .thenReturn(status = OK, body = mtdVatAuthResponse)
  }

  def agentUnauthorised(): StubMapping = {
    when(method = POST, uri = uri)
      .thenReturn(status = OK, body = agentUnauthorisedAuthResponse)
  }

  def agentAuthorised(): StubMapping = {
    when(method = POST, uri = uri)
      .thenReturn(status = OK, body = agentEnrolmentAuthResponse)
  }

  def unauthorised(): StubMapping = {
    when(method = POST, uri = uri)
      .thenReturn(status = OK, body = otherEnrolmentAuthResponse)
  }
}
