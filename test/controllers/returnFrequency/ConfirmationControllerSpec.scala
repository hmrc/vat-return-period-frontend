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

package controllers.returnFrequency

import assets.CircumstanceDetailsTestConstants._
import assets.messages.ReturnFrequencyMessages
import audit.mocks.MockAuditingService
import base.BaseSpec
import mocks.MockAuth
import mocks.services.MockCustomerCircumstanceDetailsService
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.Helpers._
import views.html.returnFrequency.{ChangeReturnFrequencyConfirmation => CRFCView}
import scala.concurrent.Future

class ConfirmationControllerSpec extends BaseSpec
  with MockAuditingService
  with MockCustomerCircumstanceDetailsService
  with MockAuth {

  val changeReturnFrequencyConfirmationView: CRFCView = injector.instanceOf[CRFCView]

  object TestChangeReturnFrequencyConfirmation extends ConfirmationController(
    mockAuthPredicate,
    mockCustomerDetailsService,
    changeReturnFrequencyConfirmationView,
    mcc
  )

  "Calling the .show action" when {

    "the user is authorised" when {

      "the user is an agent" when {

        "the call to the customer details service is successful" should {

          lazy val result = {
            mockAuthoriseAsAgent(agentAuthorisedResponse, Future.successful(agentServicesEnrolment))
            mockCustomerDetailsSuccess(circumstanceDetailsNoPending)
            TestChangeReturnFrequencyConfirmation.show(agentUser)
          }

          "return 200" in {
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "render the confirmation view" in {
            Jsoup.parse(contentAsString(result)).title shouldBe ReturnFrequencyMessages.ReceivedPage.titleAgent
          }
        }

        "the call to the customer details service is unsuccessful" should {

          lazy val result = {
            mockAuthoriseAsAgent(agentAuthorisedResponse, Future.successful(agentServicesEnrolment))
            mockCustomerDetailsError()
            TestChangeReturnFrequencyConfirmation.show(agentUser)
          }

          "return 200" in {
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "render the confirmation view" in {
            Jsoup.parse(contentAsString(result)).title shouldBe ReturnFrequencyMessages.ReceivedPage.titleAgent
          }
        }
      }

      "the user is not an agent" when {

        "display the correct content for a user that has a digital contact preference" when {

            "the call to customer circumstance details is successful" should {

              lazy val result = {
                mockAuthorise(mtdVatAuthorisedResponse)
                mockCustomerDetailsSuccess(circumstanceDetailsModelMax)
                TestChangeReturnFrequencyConfirmation.show(fakeRequest)
              }

              "return 200" in {
                status(result) shouldBe Status.OK
              }

              "return HTML" in {
                contentType(result) shouldBe Some("text/html")
                charset(result) shouldBe Some("utf-8")
              }
            }

            "the call to customer circumstance details returns an error" should {

              lazy val result = {
                mockAuthorise(mtdVatAuthorisedResponse)
                mockCustomerDetailsError()
                TestChangeReturnFrequencyConfirmation.show(fakeRequest)
              }

              "return 200" in {
                status(result) shouldBe Status.OK
              }

              "return HTML" in {
                contentType(result) shouldBe Some("text/html")
                charset(result) shouldBe Some("utf-8")
              }
            }
          }
        }
      }

      "the user is insolvent and not continuing to trade" should {

        "return 403 (Forbidden)" in {
          mockAuthorise(mtdVatAuthorisedResponse)
          lazy val result = TestChangeReturnFrequencyConfirmation.show(insolventRequest)
          status(result) shouldBe Status.FORBIDDEN
        }
      }

    authControllerChecks(TestChangeReturnFrequencyConfirmation.show, fakeRequest)
  }
}
