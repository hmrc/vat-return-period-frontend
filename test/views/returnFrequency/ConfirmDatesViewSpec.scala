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

package views.returnFrequency

import assets.messages.{ReturnFrequencyMessages => viewMessages}
import models.returnFrequency._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.returnFrequency.ConfirmDates

class ConfirmDatesViewSpec extends ViewBaseSpec {

  val confirmDatesView: ConfirmDates = injector.instanceOf[ConfirmDates]

  "Rendering the Confirm Dates page" when {

    "user doesn't come from annual accounting" should {

      lazy val view = confirmDatesView(Jan, false)(user, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title of '${viewMessages.ConfirmPage.title}'" in {
        document.title shouldBe viewMessages.ConfirmPage.title
      }

      s"display the correct page heading of '${viewMessages.ConfirmPage.heading}'" in {
        elementText(".govuk-heading-l") shouldBe viewMessages.ConfirmPage.heading
      }

      s"display the correct h2 page heading of '${viewMessages.ConfirmPage.heading2}'" in {
        elementText("h2") shouldBe viewMessages.ConfirmPage.heading2
      }

      s"display VAT return dates message" when {

        s"the current date is '${viewMessages.ConfirmPage.newDates}'" in {
          elementText(".govuk-summary-list__key") shouldBe viewMessages.ConfirmPage.newDates
        }
        s"display the correct dates of" when {

          s"the current date is '${viewMessages.option1Jan}'" in {
            lazy val view = confirmDatesView(Jan, false)(user, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)
            elementText(".govuk-summary-list__value") shouldBe viewMessages.option1Jan
          }

          s"the current date is '${viewMessages.option2Feb}'" in {
            lazy val view = confirmDatesView(Feb, false)(user, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)
            elementText(".govuk-summary-list__value") shouldBe viewMessages.option2Feb
          }

          s"the current date is '${viewMessages.option3Mar}'" in {
            lazy val view = confirmDatesView(Mar, false)(user, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)
            elementText(".govuk-summary-list__value") shouldBe viewMessages.option3Mar
          }

          s"the current date is '${viewMessages.option4Monthly}'" in {
            lazy val view = confirmDatesView(Monthly, false)(user, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)
            elementText(".govuk-summary-list__value") shouldBe viewMessages.option4Monthly
          }
        }
      }

      "have a link back to the change dates page" which {

        s"has the text '${viewMessages.ConfirmPage.changeLink}'" in {
          elementText(".govuk-summary-list__actions") shouldBe viewMessages.ConfirmPage.changeLink
        }

        "has a URL back to the change dates page" in {
          element(".govuk-summary-list__actions a").attr("href") shouldBe controllers.returnFrequency.routes.ChooseDatesController.show.url
        }
      }

      "have a continue button" which {

        s"has the text '${viewMessages.continue}'" in {
          elementText(".govuk-button") shouldBe viewMessages.continue
        }

        "posts data to the correct endpoint" in {
          element("form").attr("action") shouldBe controllers.returnFrequency.routes.ConfirmVatDatesController.submit.url
        }

        "has the prevent double click attribute" in {
          element(".govuk-button").hasAttr("data-prevent-double-click") shouldBe true
        }
      }
    }
    "user comes from annual accounting" should {

      lazy val view = confirmDatesView(Jan, true)(user, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title of '${viewMessages.ConfirmPage.title}'" in {
        document.title shouldBe viewMessages.ConfirmPage.title
      }

      s"display the correct page heading of '${viewMessages.ConfirmPage.heading}'" in {
        elementText(".govuk-heading-l") shouldBe viewMessages.ConfirmPage.heading
      }

      s"have a correct annual accounting messages" in {
        elementText(".govuk-body") shouldBe viewMessages.ConfirmPage.annualAccountingOption
        elementText(".govuk-list--bullet > li:nth-child(1)") shouldBe viewMessages.ConfirmPage.annualAccountingBullet1
        elementText(".govuk-list--bullet > li:nth-child(2)") shouldBe viewMessages.ConfirmPage.annualAccountingBullet2

      }
    }
  }
}
