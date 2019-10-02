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

package views.returnFrequency

import assets.messages.{ReturnFrequencyMessages => viewMessages}
import models.returnFrequency._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class ConfirmDatesViewSpec extends ViewBaseSpec {

  "Rendering the Confirm Dates page" should {

    lazy val view = views.html.returnFrequency.confirm_dates(Jan)(user, messages, mockAppConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title of '${viewMessages.ConfirmPage.title}'" in {
      document.title shouldBe viewMessages.ConfirmPage.title
    }

    s"have a the back link with correct text and url '${viewMessages.back}'" in {
      elementText(".link-back") shouldBe viewMessages.back
      element(".link-back").attr("href") shouldBe controllers.returnFrequency.routes.ChooseDatesController.show().url
    }

    s"have a the correct page heading of '${viewMessages.ConfirmPage.heading}'" in {
      elementText("#page-heading") shouldBe viewMessages.ConfirmPage.heading
    }

    s"have a the display the correct dates of" when {

      s"the current date is '${viewMessages.option1Jan}'" in {
        lazy val view = views.html.returnFrequency.confirm_dates(Jan)(user, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText("#p1") shouldBe s"${viewMessages.ConfirmPage.newDates} ${viewMessages.option1Jan}"
      }

      s"the current date is '${viewMessages.option2Feb}'" in {
        lazy val view = views.html.returnFrequency.confirm_dates(Feb)(user, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText("#p1") shouldBe s"${viewMessages.ConfirmPage.newDates} ${viewMessages.option2Feb}"
      }

      s"the current date is '${viewMessages.option3Mar}'" in {
        lazy val view = views.html.returnFrequency.confirm_dates(Mar)(user, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText("#p1") shouldBe s"${viewMessages.ConfirmPage.newDates} ${viewMessages.option3Mar}"
      }

      s"the current date is '${viewMessages.option4Monthly}'" in {
        lazy val view = views.html.returnFrequency.confirm_dates(Monthly)(user, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText("#p1") shouldBe s"${viewMessages.ConfirmPage.newDates} ${viewMessages.option4Monthly}"
      }
    }

    "have a link back to the change dates page" which {

      s"has the text '${viewMessages.ConfirmPage.changeLink}'" in {
        elementText("#change-vat-link") shouldBe viewMessages.ConfirmPage.changeLink
      }

      "has a URL back to the change dates page" in {
        element("#change-vat-link").attr("href") shouldBe controllers.returnFrequency.routes.ChooseDatesController.show().url
      }
    }

    "have a confirm button" which {

      s"has the text '${viewMessages.confirmAndContinue}'" in {
        elementText("#continue-button") shouldBe viewMessages.confirmAndContinue
      }

      "posts data to the correct endpoint" in {
        element("form").attr("action") shouldBe controllers.returnFrequency.routes.ConfirmVatDatesController.submit().url
      }
    }
  }
}