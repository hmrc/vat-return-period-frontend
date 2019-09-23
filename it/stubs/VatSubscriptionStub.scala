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

package stubs

import base.BaseISpec
import models.circumstanceInfo.{CircumstanceDetails, CustomerDetails}
import models.returnFrequency.Monthly
import play.api.libs.json.{JsValue, Json}

object VatSubscriptionStub extends BaseISpec {

  val orgName = "Test Organisation Name"
  val tradingName = "Test Trading Name"
  val firstName = "Test"
  val lastName = "Name"
  val partyType = "2"

  val circumstanceDetailsJsonMax: JsValue = Json.obj(
    "returnPeriod" -> Monthly,
    "changeIndicators" -> Json.obj(
      "returnPeriod" -> true
    ),
    "partyType" -> Some("2")
  )

  val circumstanceDetailsJsonMin: JsValue = Json.obj()

  val circumstanceDetailsModelMax =
    CircumstanceDetails(
      CustomerDetails(Some("bob"), Some("smith"), Some("org name"), Some("trading name")),
      Some(Monthly),
      Some(true),
      Some(partyType)
    )

  val circumstanceDetailsModelMin =
    CircumstanceDetails(
      CustomerDetails(None, None, None, None),
      None,
      None,
      None
    )
}
