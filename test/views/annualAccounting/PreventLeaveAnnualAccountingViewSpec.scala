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

package views.annualAccounting

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.annualAccounting.PreventLeaveAnnualAccounting

class PreventLeaveAnnualAccountingViewSpec extends ViewBaseSpec {

  val preventLeaveAnnualAccountingView: PreventLeaveAnnualAccounting = injector.instanceOf[PreventLeaveAnnualAccounting]

  "Rendering the preventLeaveAnnualAccounting page" should {

    lazy val view = preventLeaveAnnualAccountingView()(user,messages,mockAppConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have a back button" in {
      elementText(".govuk-back-link")  shouldBe "Back"
    }
    "have a back button with the correct redirect url" in {

      element(".govuk-back-link").attr("href") shouldBe "/manage-vat"
    }

    "have the correct heading" in {
      elementText(".govuk-heading-l")  shouldBe "You already have a change pending"
    }
    "have the correct 1st sentence" in {
      elementText("p:nth-child(2)")  shouldBe "You recently requested to change your annual accounting scheme."
    }
    "have the correct 2nd sentence" in {
      elementText("p:nth-child(3)")  shouldBe "Until this has happened, you cannot change the VAT Return dates."
    }
  }
}

