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
import play.api.libs.json.{JsObject, JsValue, Json}

object VatSubscriptionStub extends BaseISpec {

  val orgName = "Test Organisation Name"
  val tradingName = "Test Trading Name"
  val firstName = "Test"
  val lastName = "Name"

  val customerDetailsMax = CustomerDetails(
    Some(firstName),
    Some(lastName),
    Some(orgName),
    Some(tradingName)
  )

  val customerDetailsMin = CustomerDetails(
    None,
    None,
    None,
    None
  )

  val partyType = "2"

  val customerDetailsJsonMax: JsObject = Json.obj(
    "organisationName" -> orgName,
    "firstName" -> firstName,
    "lastName" -> lastName,
    "tradingName" -> tradingName
  )

  val customerDetailsJsonMin: JsObject = Json.obj()

  val circumstanceDetailsJsonMax: JsValue = Json.obj(
    "customerDetails" -> customerDetailsJsonMax,
    "returnPeriod" -> Monthly,
    "partyType" -> Some(partyType)
  )

  val circumstanceDetailsJsonMin: JsValue = Json.obj(
    "customerDetails" -> customerDetailsJsonMin
  )

  val circumstanceDetailsModelMax =
    CircumstanceDetails(
      customerDetailsMax,
      Some(Monthly),
      Some(partyType)
    )

  val circumstanceDetailsModelMin =
    CircumstanceDetails(
      customerDetailsMin,
      None,
      None
    )



}
