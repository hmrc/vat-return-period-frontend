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

package controllers.returnFrequency

import assets.BaseTestConstants._
import assets.CircumstanceDetailsTestConstants._
import assets.messages.AuthMessages
import audit.mocks.MockAuditingService
import base.BaseSpec
import common.SessionKeys
import mocks.MockAuth
import mocks.services.{MockCustomerCircumstanceDetailsService, MockReturnFrequencyService}
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.Helpers._

class ConfirmVatDatesControllerSpec extends BaseSpec
  with MockReturnFrequencyService
  with MockAuditingService
  with MockCustomerCircumstanceDetailsService
  with MockAuth {

  object TestConfirmVatDatesController extends ConfirmVatDatesController(
    mockAuthPredicate,
    errorHandler,
    mockReturnFrequencyService,
    mockCustomerDetailsService,
    mockAuditService,
    mockInFlightReturnPeriodPredicate,
    mockAppConfig,
    messagesApi
  )

  "Calling the .show action" when {

    "user is authorised" when {

      "current return frequency is in session" when {

        "new return frequency is in session" should {

          lazy val result = TestConfirmVatDatesController.show(fakeRequest.withSession(
            SessionKeys.CURRENT_RETURN_FREQUENCY -> "March",
            SessionKeys.NEW_RETURN_FREQUENCY -> "January")
          )

          lazy val document = Jsoup.parse(bodyOf(result))

          "return 200" in {
            mockAuthorise(mtdVatAuthorisedResponse)
            status(result) shouldBe Status.OK
          }
//
//          "return HTML" in {
//            contentType(result) shouldBe Some("text/html")
//            charset(result) shouldBe Some("utf-8")
//          }
//
//          "render the Confirm Dates Page" in {
//            document.title shouldBe Messages.ConfirmPage.title
//          }
        }

        "new return frequency is not in session" should {

          lazy val result = TestConfirmVatDatesController.show(fakeRequest.withSession(
            SessionKeys.CURRENT_RETURN_FREQUENCY -> "March"
          ))

          "return 303" in {
            mockAuthorise(mtdVatAuthorisedResponse)
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.returnFrequency.routes.ChooseDatesController.show().url}" in {
            redirectLocation(result) shouldBe Some(controllers.returnFrequency.routes.ChooseDatesController.show().url)
          }
        }
      }

      "current return frequency is not in session" should {

        lazy val result = TestConfirmVatDatesController.show(fakeRequest)

        "return 303" in {
          mockAuthorise(mtdVatAuthorisedResponse)
          mockCustomerDetailsSuccess(circumstanceDetailsNoPending)
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to ${controllers.returnFrequency.routes.ChooseDatesController.show().url}" in {
          redirectLocation(result) shouldBe Some(controllers.returnFrequency.routes.ChooseDatesController.show().url)
        }
      }
    }

    authControllerChecks(TestConfirmVatDatesController.show, fakeRequest)
  }

  "Calling the .submit action" when {

    "user is authorised" when {

      "current return frequency is in session" when {

        "new return frequency is in session" when {

          "updateReturnFrequency returns an error" should {

            lazy val result = TestConfirmVatDatesController.submit(fakeRequest.withSession(
              SessionKeys.NEW_RETURN_FREQUENCY -> "Monthly",
              SessionKeys.CURRENT_RETURN_FREQUENCY -> "January"
            ))

            "return 500" in {
              mockAuthorise(mtdVatAuthorisedResponse)
              setupMockCustomerDetails(vrn)(Right(circumstanceDetailsNoPending))
              setupMockReturnFrequencyServiceWithFailure()
              status(result) shouldBe Status.INTERNAL_SERVER_ERROR
              messages(Jsoup.parse(bodyOf(result)).title) shouldBe AuthMessages.problemWithServiceTitle + AuthMessages.mtdfvTitleSuffix
            }
          }

          "updateReturnFrequency returns success" should {

            lazy val result = TestConfirmVatDatesController.submit(fakeRequest.withSession(
              SessionKeys.NEW_RETURN_FREQUENCY -> "January",
              SessionKeys.CURRENT_RETURN_FREQUENCY -> "Monthly"
            ))

            "return 303" in {
              mockAuthorise(mtdVatAuthorisedResponse)
              setupMockReturnFrequencyServiceWithSuccess()
              setupMockCustomerDetails(vrn)(Right(circumstanceDetailsNoPending))
              setupAuditExtendedEvent()

              status(result) shouldBe Status.SEE_OTHER
            }

            s"redirect to ${controllers.returnFrequency.routes.ChangeReturnFrequencyConfirmation.show("non-agent").url}" in {
              redirectLocation(result) shouldBe Some(controllers.returnFrequency.routes.ChangeReturnFrequencyConfirmation.show("non-agent").url)
            }
          }
        }

        "new return frequency is not in session" should {

          lazy val result = TestConfirmVatDatesController.submit(fakeRequest.withSession(
            SessionKeys.CURRENT_RETURN_FREQUENCY -> "January"
          ))

          "return 303" in {
            mockAuthorise(mtdVatAuthorisedResponse)
            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${controllers.returnFrequency.routes.ChooseDatesController.show().url}" in {
            redirectLocation(result) shouldBe Some(controllers.returnFrequency.routes.ChooseDatesController.show().url)
          }
        }
      }

      "current return frequency is not in session" should {

        lazy val result = TestConfirmVatDatesController.submit(fakeRequest.withSession(
          SessionKeys.CURRENT_RETURN_FREQUENCY -> "January"
        ))

        "return 303" in {
          mockAuthorise(mtdVatAuthorisedResponse)
          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to ${controllers.returnFrequency.routes.ChooseDatesController.show().url}" in {
          redirectLocation(result) shouldBe Some(controllers.returnFrequency.routes.ChooseDatesController.show().url)
        }
      }
    }
  }
}
