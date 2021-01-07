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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.circumstanceInfo.{ChangeIndicators, CircumstanceDetails, CustomerDetails}
import models.contactPreferences.ContactPreference.digital
import models.returnFrequency.Monthly
import play.api.http.Status.OK
import play.api.libs.json.{JsValue, Json}
import utils.WireMockMethods

object VatSubscriptionStub extends WireMockMethods {

  private val subscriptionUri: String => String = vrn => s"/vat-subscription/$vrn/full-information"

  val partyType = "2"

  val circumstanceDetailsJsonMax: JsValue = Json.obj(
    "customerDetails" -> Json.obj(
      "firstName" -> "bob",
      "lastName" -> "smith",
      "organisationName" -> "org name",
      "tradingName" -> "trading name"
    ),
    "ppob" -> Json.obj(
      "contactDetails" -> Json.obj(
        "emailVerified" -> true)),
    "returnPeriod" -> Monthly,
    "changeIndicators" -> Json.obj(
      "returnPeriod" -> true,
      "annualAccounting" -> false
    ),
    "partyType" -> Some("2"),
    "commsPreference" -> digital
  )

  val circumstanceDetailsModelMax: CircumstanceDetails =
    CircumstanceDetails(
      CustomerDetails(Some("bob"), Some("smith"), Some("org name"), Some("trading name")),
      Some(ChangeIndicators(Some(true))),
      Some(Monthly),
      Some(partyType),
      Some(true),
      Some(digital)
    )

  val circumstanceDetailsModelMin: CircumstanceDetails =
    CircumstanceDetails(
      CustomerDetails(None, None, None, None),
      None,
      None,
      None,
      None,
      None
    )

  def getClientDetailsSuccess(vrn: String)(customerDetails: CircumstanceDetails): StubMapping =
    when(method = GET, uri = subscriptionUri(vrn))
      .thenReturn(status = OK, body = Json.toJson(customerDetails))
}
