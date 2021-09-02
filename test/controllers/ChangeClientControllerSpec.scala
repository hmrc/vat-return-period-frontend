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

package controllers

import base.BaseSpec
import common.SessionKeys
import mocks.MockAuth
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.BearerTokenExpired

import scala.concurrent.Future

class ChangeClientControllerSpec extends BaseSpec with MockAuth {

  object TestConfirmClientVrnControllerSpec extends ChangeClientController(
    mockAuthPredicate,
    mockAppConfig,
    mcc
  )

  "Calling the .changeClient action" when {

    "the user is an Agent" when {

      "the Agent is authorised and signed up to HMRC-AS-AGENT" when {

        "a Clients VRN is held in Session" should {

          lazy val request = FakeRequest().withSession(
            SessionKeys.CLIENT_VRN -> vrn,
            SessionKeys.NEW_RETURN_FREQUENCY -> "Jan",
            SessionKeys.CURRENT_RETURN_FREQUENCY -> "Monthly"
          )

          lazy val result = TestConfirmClientVrnControllerSpec.changeClient(request)

          "return status redirect SEE_OTHER (303)" in {
            mockAuthoriseAsAgent(agentAuthorisedResponse, Future.successful(agentServicesEnrolment))
            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect to the Select Your Client show action" in {
            redirectLocation(result) shouldBe
              Some(mockAppConfig.agentClientLookupStartUrl(mockAppConfig.manageVatUrl))
          }

          "have removed the Clients VRN from session" in {
            session(result).get(SessionKeys.CLIENT_VRN) shouldBe None
          }

          "have removed the ReturnFrequency from session" in {
            session(result).get(SessionKeys.NEW_RETURN_FREQUENCY) shouldBe None
          }

          "have removed the CurrentReturnFrequency from session" in {
            session(result).get(SessionKeys.CURRENT_RETURN_FREQUENCY) shouldBe None
          }
        }
      }
    }

    "the user is not authenticated" should {

      "return 401 (Unauthorised)" in {
        mockAuthorise(Future.failed(BearerTokenExpired()))
        val result = TestConfirmClientVrnControllerSpec.changeClient(fakeRequestWithClientsVRN)
        status(result) shouldBe Status.SEE_OTHER
      }
    }
  }
}
