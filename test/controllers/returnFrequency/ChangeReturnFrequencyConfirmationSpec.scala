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

import assets.CircumstanceDetailsTestConstants._
import assets.messages.ReturnFrequencyMessages
import audit.mocks.MockAuditingService
import base.BaseSpec
import mocks.MockAuth
import mocks.services.{MockContactPreferenceService, MockCustomerCircumstanceDetailsService}
import models.contactPreferences.ContactPreference
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.Helpers._

class ChangeReturnFrequencyConfirmationSpec extends BaseSpec
  with MockContactPreferenceService
  with MockAuditingService
  with MockCustomerCircumstanceDetailsService
  with MockAuth {

  object TestChangeReturnFrequencyConfirmation extends ChangeReturnFrequencyConfirmation(
    messagesApi,
    mockAuthPredicate,
    mockCustomerDetailsService,
    mockContactPreferenceService,
    errorHandler,
    mockAuditService,
    mockAppConfig
  )

  "Calling the .show action" when {

    "the user is authorised" when {

      "the user is an agent" when {

        "the call to the customer details service is successful" should {

          lazy val result = {
            mockAuthoriseAsAgent(agentAuthorisedResponse, agentServicesEnrolment)
            mockCustomerDetailsSuccess(circumstanceDetailsNoPending)
            TestChangeReturnFrequencyConfirmation.show("agent")(agentUser)
          }

          "return 200" in {
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "render the confirmation view" in {
            Jsoup.parse(bodyOf(result)).title shouldBe ReturnFrequencyMessages.ReceivedPage.titleAgent
          }
        }

        "the call to the customer details service is unsuccessful" should {

          lazy val result = {
            mockAuthoriseAsAgent(agentAuthorisedResponse, agentServicesEnrolment)
            mockCustomerDetailsError()
            TestChangeReturnFrequencyConfirmation.show("agent")(agentUser)
          }

          "return 200" in {
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "render the confirmation view" in {
            Jsoup.parse(bodyOf(result)).title shouldBe ReturnFrequencyMessages.ReceivedPage.titleAgent
          }
        }
      }

      "the user is not an agent" when {

        "display the correct content for a user that has a digital contact preference" should {

          lazy val result = {
            mockAuthorise(mtdVatAuthorisedResponse)
            mockContactPreferenceSuccess(ContactPreference("DIGITAL"))
            setupAuditExtendedEvent()
            TestChangeReturnFrequencyConfirmation.show(user.redirectSuffix)(fakeRequest)
          }

          lazy val document = Jsoup.parse(bodyOf(result))

          "return 200" in {
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "render the Change Return Frequency Confirmation Page" in {
            document.title shouldBe ReturnFrequencyMessages.ReceivedPage.title
            document.select("#content article p:nth-of-type(1)").text() shouldBe ReturnFrequencyMessages.ReceivedPage.digitalPref
          }
        }

        "display the correct content for a user that has a paper contact preference" should {

          lazy val result = {
            mockAuthorise(mtdVatAuthorisedResponse)
            mockContactPreferenceSuccess(ContactPreference("PAPER"))
            setupAuditExtendedEvent()
            TestChangeReturnFrequencyConfirmation.show(user.redirectSuffix)(fakeRequest)
          }

          lazy val document = Jsoup.parse(bodyOf(result))

          "return 200" in {
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "render the Change Return Frequency Confirmation Page" in {
            document.title shouldBe ReturnFrequencyMessages.ReceivedPage.title
            document.select("#content article p:nth-of-type(1)").text() shouldBe ReturnFrequencyMessages.ReceivedPage.paperPref
          }
        }

        "display the correct content when an error is returned from contactPreferences" should {

          lazy val result = {
            mockAuthorise(mtdVatAuthorisedResponse)
            mockContactPreferenceError()
            TestChangeReturnFrequencyConfirmation.show(user.redirectSuffix)(fakeRequest)
          }

          lazy val document = Jsoup.parse(bodyOf(result))

          "return 200" in {
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "render the Change Return Frequency Confirmation Page" in {
            document.title shouldBe ReturnFrequencyMessages.ReceivedPage.title
            document.select("#content article p:nth-of-type(1)").text() shouldBe ReturnFrequencyMessages.ReceivedPage.contactPrefError
          }
        }
      }
    }

    authControllerChecks(TestChangeReturnFrequencyConfirmation.show(user.redirectSuffix), fakeRequest)
  }
}