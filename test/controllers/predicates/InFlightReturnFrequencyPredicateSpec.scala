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

package controllers.predicates

import assets.CircumstanceDetailsTestConstants._
import assets.ReturnPeriodTestConstants._
import assets.messages.AuthMessages
import common.SessionKeys
import mocks.MockAuth
import mocks.services.MockCustomerCircumstanceDetailsService
import models.auth.User
import org.jsoup.Jsoup
import play.api.test.FakeRequest
import play.api.test.Helpers._

class InFlightReturnFrequencyPredicateSpec extends MockAuth with MockCustomerCircumstanceDetailsService {

  "The InFlightReturnFrequencyPredicate" when {

    "user has CURRENT_RETURN_FREQUENCY in session" should {

      lazy val fakeRequest = FakeRequest().withSession(
        SessionKeys.CURRENT_RETURN_FREQUENCY -> "Monthly"
      )

      lazy val result = {
        await(mockInFlightReturnPeriodPredicate.refine(User(vrn)(fakeRequest)))
      }

      "allow the request through" in {
        result shouldBe Right(user)
      }
    }

    "user has no CURRENT_RETURN_FREQUENCY in session" when {

      "getCustomerCircumstanceDetails call fails" should {

        lazy val result = {
          mockCustomerDetailsError()
          await(mockInFlightReturnPeriodPredicate.refine(user)).left.get
        }

        "return 500" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
          messages(Jsoup.parse(bodyOf(result)).title) shouldBe AuthMessages.problemWithServiceTitle + AuthMessages.mtdfvTitleSuffix
        }
      }

      "getCustomerCircumstanceDetails call is successful" when {

        "return period change indicator is true" should {

          lazy val result = {
            mockCustomerDetailsSuccess(circumstanceDetailsModelMax)
            await(mockInFlightReturnPeriodPredicate.refine(user).left.get)
          }

          "return 303" in {
            status(result) shouldBe SEE_OTHER
          }

          s"redirect to ${mockAppConfig.manageVatUrl}" in {
            redirectLocation(result) shouldBe Some(mockAppConfig.manageVatUrl)
          }
        }

        "return period change indicator is false" when {

          "no return period is returned" should {

            lazy val result = {
              mockCustomerDetailsSuccess(circumstanceDetailsModelMin)
              await(mockInFlightReturnPeriodPredicate.refine(user).left.get)
            }

            "return 303" in {
              status(result) shouldBe SEE_OTHER
            }

            s"redirect to ${mockAppConfig.manageVatUrl}" in {
              redirectLocation(result) shouldBe Some(mockAppConfig.manageVatUrl)
            }
          }

          "return period is returned" should {

            lazy val result = {
              mockCustomerDetailsSuccess(circumstanceDetailsNoPending)
              await(mockInFlightReturnPeriodPredicate.refine(user).left.get)
            }

            "return 303" in {
              status(result) shouldBe SEE_OTHER
            }

            s"redirect to ${controllers.returnFrequency.routes.ChooseDatesController.show().url}" in {
              redirectLocation(result) shouldBe Some(controllers.returnFrequency.routes.ChooseDatesController.show().url)
            }

            "add the current return frequency to the session" in {
              session(result).get(SessionKeys.CURRENT_RETURN_FREQUENCY) shouldBe Some(returnPeriodMonthly)
            }
          }
        }

        "changeIndicators is not returned" when {

          "return period is returned" should {

            lazy val result = {
              mockCustomerDetailsSuccess(circumstanceDetailsNoChangeIndicator)
              await(mockInFlightReturnPeriodPredicate.refine(user).left.get)
            }

            "return 303" in {
              status(result) shouldBe SEE_OTHER
            }

            s"redirect to ${controllers.returnFrequency.routes.ChooseDatesController.show().url}" in {
              redirectLocation(result) shouldBe Some(controllers.returnFrequency.routes.ChooseDatesController.show().url)
            }

            "add the current return frequency to the session" in {
              session(result).get(SessionKeys.CURRENT_RETURN_FREQUENCY) shouldBe Some(returnPeriodMonthly)
            }
          }
        }
      }
    }
  }
}
