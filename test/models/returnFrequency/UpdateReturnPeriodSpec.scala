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

package models.returnFrequency

import assets.UpdateReturnPeriodTestConstants._
import play.api.libs.json.Json
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

class UpdateReturnPeriodSpec extends AnyWordSpecLike with Matchers {

  "UpdateReturnPeriod" should {

    "serialize to JSON" when {

      "all optional fields are populated" in {
        Json.toJson(updateReturnPeriodMax) shouldBe updateReturnPeriodJsonMax
      }

      "no optional fields are returned" in {
        Json.toJson(updateReturnPeriodMin) shouldBe updateReturnPeriodJsonMin
      }
    }
  }
}
