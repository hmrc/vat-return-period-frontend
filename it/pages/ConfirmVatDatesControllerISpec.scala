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

package pages

import common.SessionKeys
import models.returnFrequency.SubscriptionUpdateResponseModel
import play.api.http.Status.{INTERNAL_SERVER_ERROR, SEE_OTHER}
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.test.Helpers.{FORBIDDEN, OK}
import stubs.ReturnFrequencyStub._
import stubs.VatSubscriptionStub._

class ConfirmVatDatesControllerISpec extends BasePageISpec{

  val path = "/confirm-vat-return-dates"
  val sessionWithReturnFrequency: Map[String, String] = Map(
    SessionKeys.CLIENT_VRN -> "999999999",
    SessionKeys.NEW_RETURN_FREQUENCY -> "January",
    SessionKeys.CURRENT_RETURN_FREQUENCY -> "Monthly",
    SessionKeys.ANNUAL_ACCOUNTING_PENDING -> "false",
    SessionKeys.insolventWithoutAccessKey -> "false"
  )

  "Calling ConfirmVatDatesController.show" when {

    def show: WSResponse = get(path, sessionWithReturnFrequency)

    "A valid ReturnPeriod is returned" should {

      "render the page for an individual" in {
        given.user.isAuthenticated

        When("I call to show the Customer Circumstances page")
        val res = show

        res should have(
          httpStatus(OK),
          elementText(".govuk-heading-l")(Messages("checkYourAnswers.title")),
          elementText(".govuk-summary-list__key")(Messages("checkYourAnswers.newDates")),
          elementText(".govuk-summary-list__value")(Messages("January, April, July and October"))
        )
      }

      "render the page for a agent signed up to agent services" in {

        given.agent.isSignedUpToAgentServices

        When("I call to show the Customer Circumstances page")
        val res = show

        res should have(
          httpStatus(OK),
          elementText(".govuk-heading-l")(Messages("checkYourAnswers.title")),
          elementText(".govuk-summary-list__key")(Messages("checkYourAnswers.newDates")),
          elementText(".govuk-summary-list__value")(Messages("January, April, July and October"))
        )
      }
    }
  }

  "Calling ConfirmVatDatesController.submit" when {

    "the user is an individual" should {

      "A valid ReturnPeriod is returned" should {

        "render the ChangeReturnFrequencyConfirmation page" in {

          given.user.isAuthenticated

          And("I stub a successful response from the Payments service")
          putSubscriptionSuccess(SubscriptionUpdateResponseModel("Good times"))
          getClientDetailsSuccess("999999999")(circumstanceDetailsJsonMin)

          When("I initiate a return frequency update journey")
          val res: WSResponse = postJSValueBody(
            "/confirm-vat-return-dates",
            sessionWithReturnFrequency
          )(Json.obj("body" -> "anything"))

          res should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.returnFrequency.routes.ConfirmationController.show("non-agent").url)
          )
        }
      }

      "An invalid model is posted" should {

        "Render the Internal Server Error page" in {

          given.user.isAuthenticated

          And("I stub an error response from the Payments service")
          putSubscriptionError()

          When("I initiate a return frequency update journey")
          val res = postJSValueBody(
            "/confirm-vat-return-dates",
            sessionWithReturnFrequency
          )(Json.obj("body" -> "anything"))

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )
        }
      }
    }

    "the user is an agent" should {

      "if a valid ReturnPeriod is returned" should {

        "render the ChangeReturnFrequencyConfirmation page" in {

          given.agent.isSignedUpToAgentServices

          And("I stub a successful response from the Payments service")
          putSubscriptionSuccess(SubscriptionUpdateResponseModel("Good times"))

          When("I initiate a return frequency update journey")
          val res: WSResponse = postJSValueBody(
            "/confirm-vat-return-dates",
            sessionWithReturnFrequency
          )(Json.obj("body" -> "anything"))

          res should have(
            httpStatus(SEE_OTHER),
            redirectURI(controllers.returnFrequency.routes.ConfirmationController.show("agent").url)
          )
        }
      }

      "if an invalid model is posted" should {

        "Render the Internal Server Error page" in {

          given.agent.isSignedUpToAgentServices

          And("I stub an error response from the Payments service")
          putSubscriptionError()

          When("I initiate a return frequency update journey")
          val res = postJSValueBody(
            "/confirm-vat-return-dates",
            sessionWithReturnFrequency
          )(Json.obj("body" -> "anything"))

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )
        }
      }

      "the Agent is NOT signed up for HMRC-AS-AGENT (unauthorised)" when {

        "render the Agent Unauthorised page" in {

          given.agent.isNotSignedUpToAgentServices

          And("I stub a successful response from the Payments service")
          putSubscriptionSuccess(SubscriptionUpdateResponseModel("Good times"))

          When("I submit the Client VRN page with valid data")
          val res: WSResponse = postJSValueBody(
            "/confirm-vat-return-dates",
            sessionWithReturnFrequency
          )(Json.obj("body" -> "anything"))

          res should have(
            httpStatus(FORBIDDEN),
            pageTitle("You can’t use this service yet" + titleSuffixOther)
          )
        }
      }
    }
  }
}
