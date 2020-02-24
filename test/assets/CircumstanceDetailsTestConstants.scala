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

package assets

import models.circumstanceInfo.{ChangeIndicators, CircumstanceDetails, CustomerDetails}
import models.returnFrequency.Monthly
import play.api.libs.json.{JsValue, Json}

object CircumstanceDetailsTestConstants {

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
    "partyType" -> Some(partyType)
  )

  val circumstanceDetailsJsonMin: JsValue = Json.obj(
    "customerDetails" -> Json.obj()
  )

  val circumstanceDetailsModelMax =
    CircumstanceDetails(
      CustomerDetails(Some("bob"), Some("smith"), Some("org name"), Some("trading name")),
      Some(ChangeIndicators(Some(true))),
      Some(Monthly),
      Some(partyType),
      Some(true)
    )

  val circumstanceDetailsModelMaxAA =
    CircumstanceDetails(
      CustomerDetails(Some("bob"), Some("smith"), Some("org name"), Some("trading name")),
      Some(ChangeIndicators(Some(false), annualAccounting = true)),
      Some(Monthly),
      Some(partyType),
      Some(true)
    )

  val circumstanceDetailsModelMin =
    CircumstanceDetails(
      CustomerDetails(None, None, None, None),
      None,
      None,
      None,
      None
    )

  val circumstanceDetailsModelMinAA =
    CircumstanceDetails(
      CustomerDetails(None, None, None, None),
      Some(ChangeIndicators(Some(false), annualAccounting = true)),
      None,
      None,
      None
    )

  val circumstanceDetailsNoPending =
    CircumstanceDetails(
      CustomerDetails(Some("bob"), Some("smith"), Some("org name"), Some("trading name")),
      Some(ChangeIndicators(Some(false))),
      Some(Monthly),
      Some(partyType),
      None
    )

  val circumstanceDetailsNoChangeIndicator =
    CircumstanceDetails(
      CustomerDetails(Some("bob"), Some("smith"), Some("org name"), Some("trading name")),
      None,
      Some(Monthly),
      None,
      None
    )
}
