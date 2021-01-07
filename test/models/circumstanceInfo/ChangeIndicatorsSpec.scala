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

package models.circumstanceInfo

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.play.test.UnitSpec

class ChangeIndicatorsSpec extends UnitSpec {

  val validJson: JsObject = Json.obj(
    "returnPeriod" -> true,
    "annualAccounting" -> false
  )

  val validModel: ChangeIndicators = ChangeIndicators(Some(true))

  "Change Indicator reads" should {
    "correctly render from Json" in {
      validJson.as[ChangeIndicators] shouldBe validModel
    }
  }

  "Change Indicator writes" should {
    "correctly render to Json" in {
      Json.toJson(validModel) shouldBe validJson
    }
  }

}
