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

package views.returnFrequency

import assets.messages.{ReturnFrequencyMessages => viewMessages}
import models.returnFrequency._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class ConfirmDatesViewSpec extends ViewBaseSpec {

  "Rendering the Confirm Dates page" when {

    "user doesn't come from annual accounting" should {

      lazy val view = views.html.returnFrequency.confirm_dates(Jan, false)(user, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title of '${viewMessages.ConfirmPage.title}'" in {
        document.title shouldBe viewMessages.ConfirmPage.title
      }

      s"display the correct page heading of '${viewMessages.ConfirmPage.heading}'" in {
        elementText(".heading-large") shouldBe viewMessages.ConfirmPage.heading
      }

      s"display the correct h2 page heading of '${viewMessages.ConfirmPage.heading2}'" in {
        elementText("h2") shouldBe viewMessages.ConfirmPage.heading2
      }

      s"display VAT return dates message" when {

        s"the current date is '${viewMessages.ConfirmPage.newDates}'" in {
          elementText(".cya-question") shouldBe viewMessages.ConfirmPage.newDates
        }
        s"display the correct dates of" when {

          s"the current date is '${viewMessages.option1Jan}'" in {
            lazy val view = views.html.returnFrequency.confirm_dates(Jan, false)(user, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)
            elementText(".cya-answer") shouldBe viewMessages.option1Jan
          }

          s"the current date is '${viewMessages.option2Feb}'" in {
            lazy val view = views.html.returnFrequency.confirm_dates(Feb, false)(user, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)
            elementText(".cya-answer") shouldBe viewMessages.option2Feb
          }

          s"the current date is '${viewMessages.option3Mar}'" in {
            lazy val view = views.html.returnFrequency.confirm_dates(Mar, false)(user, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)
            elementText(".cya-answer") shouldBe viewMessages.option3Mar
          }

          s"the current date is '${viewMessages.option4Monthly}'" in {
            lazy val view = views.html.returnFrequency.confirm_dates(Monthly, false)(user, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)
            elementText(".cya-answer") shouldBe viewMessages.option4Monthly
          }
        }
      }

      "have a link back to the change dates page" which {

        s"has the text '${viewMessages.ConfirmPage.changeLink}'" in {
          elementText(".cya-change") shouldBe viewMessages.ConfirmPage.changeLink
        }

        "has a URL back to the change dates page" in {
          element(".cya-change a").attr("href") shouldBe controllers.returnFrequency.routes.ChooseDatesController.show().url
        }
      }

      "have a continue button" which {

        s"has the text '${viewMessages.continue}'" in {
          elementText("#continue-button") shouldBe viewMessages.continue
        }

        "posts data to the correct endpoint" in {
          element("form").attr("action") shouldBe controllers.returnFrequency.routes.ConfirmVatDatesController.submit().url
        }
      }
    }
    "user comes from annual accounting" should {

      lazy val view = views.html.returnFrequency.confirm_dates(Jan, true)(user, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title of '${viewMessages.ConfirmPage.title}'" in {
        document.title shouldBe viewMessages.ConfirmPage.title
      }

      s"display the correct page heading of '${viewMessages.ConfirmPage.heading}'" in {
        elementText(".heading-large") shouldBe viewMessages.ConfirmPage.heading
      }


      s"have a correct annual accounting messages" in {
        elementText("#content > article > p") shouldBe viewMessages.ConfirmPage.annualAccountingOption
        elementText("#content > article > ul > li:nth-child(1)") shouldBe viewMessages.ConfirmPage.annualAccountingBullet1
        elementText("#content > article > ul > li:nth-child(2)") shouldBe viewMessages.ConfirmPage.annualAccountingBullet2

      }
    }
  }
}
