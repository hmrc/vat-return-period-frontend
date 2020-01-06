/*
 * Copyright 2020 HM Revenue & Customs
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

class preventLeaveAnnualAccountingViewSpec extends ViewBaseSpec {

  "Rendering the preventLeaveAnnualAccounting page" should {

    lazy val view = views.html.annualAccounting.preventLeaveAnnualAccounting()(user,messages,mockAppConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have a back button" in {
      elementText(".link-back")  shouldBe "Back"
    }
    "have a back button with the correct redirect url" in {

      element(".link-back").attr("href") shouldBe "/manage-vat"
    }

    "have the correct heading" in {
      elementText("h1:nth-child(1)")  shouldBe "You already have a change pending"
    }
    "have the correct 1st sentence" in {
      elementText("p:nth-child(3)")  shouldBe "You recently requested to change your annual accounting scheme."
    }
    "have the correct 2nd sentence" in {
      elementText("p:nth-child(4)")  shouldBe "Until this has happened, you cannot change the VAT Return dates."
    }
  }
}

