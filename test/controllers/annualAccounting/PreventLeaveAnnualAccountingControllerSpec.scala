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

package controllers.annualAccounting

import audit.mocks.MockAuditingService
import base.BaseSpec
import mocks.MockAuth
import mocks.services.MockCustomerCircumstanceDetailsService
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.Helpers._

class PreventLeaveAnnualAccountingControllerSpec extends BaseSpec
  with MockAuditingService
  with MockCustomerCircumstanceDetailsService
  with MockAuth{

  object TestController extends PreventLeaveAnnualAccountingController(
    messagesApi,
    mockAuthPredicate,
    mockCustomerDetailsService,
    errorHandler,
    mockAppConfig
  )

  "Calling .show" should {

    mockAuthorise(mtdVatAuthorisedResponse)
     lazy val result = TestController.show(fakeRequest)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
    "render the confirmation view" in {
      Jsoup.parse(bodyOf(result)).title shouldBe "You already have a change pending - Business tax account - GOV.UK"
    }
    authControllerChecks(TestController.show, fakeRequest)
  }
}
