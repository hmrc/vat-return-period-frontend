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

package models.circumstanceInfo

import assets.CustomerDetailsTestConstants._
import play.api.libs.json.Json
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

class CustomerDetailsSpec extends AnyWordSpecLike with Matchers {

  val exemptInsolvencyTypes: Seq[String] = customerDetailsMax.exemptInsolvencyTypes
  val blockedInsolvencyTypes: Seq[String] = customerDetailsMax.blockedInsolvencyTypes

  "CustomerDetailsModel" when {

    "calling .isOrg" should {

      "Return True when the user is an Organisation" in {
        organisation.isOrganisation shouldBe true
      }

      "Return False when the user is NOT an Organisation" in {
        individual.isOrganisation shouldBe false
      }
    }

    "calling .username" when {

      "FirstName and Lastname are present" should {

        "return 'Firstname Lastname'" in {
          individual.userName shouldBe Some(s"$firstName $lastName")
        }
      }

      "FirstName is present" should {

        "return 'Firstname'" in {
          CustomerDetails(Some(firstName), None, None, None, false, None, None).userName shouldBe Some(s"$firstName")
        }
      }

      "LastName is present" should {

        "return 'Lastname'" in {
          CustomerDetails(None, Some(lastName), None, None, false, None, None).userName shouldBe Some(s"$lastName")
        }
      }

      "No names are present" should {

        "return None" in {
          customerDetailsMin.userName shouldBe None
        }
      }
    }

    "calling .businessName" when {

      "Organisation Name is present" should {

        "return Organisation name" in {
          customerDetailsMax.businessName shouldBe Some(s"$orgName")
        }
      }

      "username is present and org name is missing" should {

        "return username" in {
          individual.businessName shouldBe Some(s"$firstName $lastName")
        }
      }

      "username and organisation anme are missing" should {

        "return username" in {
          customerDetailsMin.businessName shouldBe None
        }
      }
    }

    "calling .clientName" when {

      "Trading name is present" should {

        "return Trading Name" in {
          customerDetailsMax.clientName shouldBe Some(tradingName)
        }
      }

      "Trading name is not present" should {

        "return Business Name" in {
          individual.clientName shouldBe Some(s"$firstName $lastName")
        }
      }

      "Trading name and businessName are not present" should {

        "return None" in {
          customerDetailsMin.businessName shouldBe None
        }
      }
    }

    "Deserialize from JSON" when {

      "all optional fields are populated" in {
        customerDetailsJsonMax.as[CustomerDetails](CustomerDetails.reads) shouldBe customerDetailsMax
      }

      "no optional fields are returned" in {
        customerDetailsJsonMin.as[CustomerDetails](CustomerDetails.reads) shouldBe customerDetailsMin
      }
    }

    "Serialize to JSON" when {

      "all optional fields are populated" in {
        Json.toJson(customerDetailsMax)(CustomerDetails.writes) shouldBe customerDetailsJsonMax
      }

      "no optional fields are returned" in {
        Json.toJson(customerDetailsMin)(CustomerDetails.writes) shouldBe customerDetailsJsonMin
      }
    }

    "calling .isInsolventWithoutAccess" when {

      "the user is insolvent and has an exempt insolvency type" should {

        "return false" in {
          exemptInsolvencyTypes.foreach { value =>
            customerDetailsInsolvent.copy(insolvencyType = Some(value)).isInsolventWithoutAccess shouldBe false
          }
        }
      }
      "the user is insolvent and has a blocked insolvency type" should {

        "return true" in {
          blockedInsolvencyTypes.foreach { value =>
            customerDetailsInsolvent.copy(insolvencyType = Some(value)).isInsolventWithoutAccess shouldBe true
          }
        }
      }
      "the user is insolvent and has an insolvency type with no associated rules" when {

        "the user is continuing to trade" should {

          "return false" in {
            customerDetailsInsolvent.copy(continueToTrade = Some(true)).isInsolventWithoutAccess shouldBe false
          }
        }
        "the user is not continuing to trade" should {

          "return true" in {
            customerDetailsInsolvent.isInsolventWithoutAccess shouldBe true
          }
        }
      }
      "the user is not insolvent" should {

        "return false" in {
          customerDetailsMax.isInsolventWithoutAccess shouldBe false
        }
      }
    }
  }
}
