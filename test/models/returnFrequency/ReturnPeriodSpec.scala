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

package models.returnFrequency

import assets.ReturnPeriodTestConstants._
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

class ReturnPeriodSpec extends AnyWordSpecLike with Matchers {

  "ReturnPeriod.apply" should {

    "for 'January' should return Jan case object" in {
      ReturnPeriod(returnPeriodJan) shouldBe Some(Jan)
    }

    "for 'February' should return Feb case object" in {
      ReturnPeriod(returnPeriodFeb) shouldBe Some(Feb)
    }

    "for 'March' should return Mar case object" in {
      ReturnPeriod(returnPeriodMar) shouldBe Some(Mar)
    }

    "for 'Monthly' should return Monthly case object" in {
      ReturnPeriod(returnPeriodMonthly) shouldBe Some(Monthly)
    }

    "for 'Annually' should return Annually case object" in {
      ReturnPeriod(returnPeriodAnnually) shouldBe Some(Annually)
    }

    "for non existent id should return None" in {
      ReturnPeriod("") shouldBe None
    }
  }

  "ReturnPeriod.unapply" should {

    "for Jan case object return 'January'" in {
      ReturnPeriod.unapply(Jan) shouldBe returnPeriodJan
    }

    "for Feb case object return 'February'" in {
      ReturnPeriod.unapply(Feb) shouldBe returnPeriodFeb
    }

    "for Mar case object return 'March'" in {
      ReturnPeriod.unapply(Mar) shouldBe returnPeriodMar
    }

    "for Monthly case object return 'Monthly'" in {
      ReturnPeriod.unapply(Monthly) shouldBe returnPeriodMonthly
    }

    "for Annually case object return 'Annually'" in {
      ReturnPeriod.unapply(Annually) shouldBe returnPeriodAnnually
    }
  }

  "ReturnPeriod Reads" should {
    "parse the json correctly for MA types" in {
      returnPeriodMAJson.as[ReturnPeriod] shouldBe Jan
    }

    "parse the json correctly for MB types" in {
      returnPeriodMBJson.as[ReturnPeriod] shouldBe Feb
    }

    "parse the json correctly for MC types" in {
      returnPeriodMCJson.as[ReturnPeriod] shouldBe Mar
    }

    "parse the json correctly for MM types" in {
      returnPeriodMMJson.as[ReturnPeriod] shouldBe Monthly
    }

    "parse the json correctly for all Annual types" in {
      for((_, json) <- allAnnualKeysAsJson) {
        json.as[ReturnPeriod] shouldBe Annually
      }
    }
  }
}