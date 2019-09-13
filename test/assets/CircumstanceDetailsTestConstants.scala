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

package assets

import assets.CustomerDetailsTestConstants._
import models.circumstanceInfo.CircumstanceDetails
import models.returnFrequency.Monthly
import play.api.libs.json.{JsValue, Json}

object CircumstanceDetailsTestConstants {

  val partyType = "2"

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
