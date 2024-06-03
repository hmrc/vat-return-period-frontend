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

package assets

import models.circumstanceInfo.CustomerDetails
import play.api.libs.json.{JsObject, Json}

object CustomerDetailsTestConstants {

  val orgName = "Ancient Antiques Ltd"
  val tradingName = "Dusty Relics"
  val firstName = "Fred"
  val lastName = "Flintstone"

  val individualJson: JsObject = Json.obj(
    "firstName" -> firstName,
    "lastName" -> lastName
  )

  val organisationJson: JsObject = Json.obj(
    "organisationName" -> orgName,
    "tradingName" -> tradingName
  )

  val customerDetailsJsonMax: JsObject = Json.obj(
    "organisationName" -> orgName,
    "firstName" -> firstName,
    "lastName" -> lastName,
    "tradingName" -> tradingName,
    "isInsolvent" -> false,
    "continueToTrade" -> Some(true),
    "insolvencyType" -> Some("01")
  )

  val customerDetailsJsonMin: JsObject = Json.obj("isInsolvent" -> false)

  val customerDetailsMax = CustomerDetails(
    Some(firstName),
    Some(lastName),
    Some(orgName),
    Some(tradingName),
    false,
    Some(true),
    Some("01")
  )

  val customerDetailsMin = CustomerDetails(
    None,
    None,
    None,
    None,
    false,
    None,
    None
  )

  val organisation = CustomerDetails(
    None,
    None,
    Some("org name"),
    None,
    false,
    None,
    None
  )

  val individual = CustomerDetails(
    Some(firstName),
    Some(lastName),
    None,
    None,
    false,
    None,
    None
  )

  val customerDetailsInsolvent: CustomerDetails = customerDetailsMax.copy(isInsolvent = true, continueToTrade = Some(false))
}
