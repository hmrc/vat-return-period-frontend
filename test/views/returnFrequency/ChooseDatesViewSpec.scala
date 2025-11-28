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
import forms.ChooseDatesForm
import models.returnFrequency._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import views.ViewBaseSpec
import views.html.returnFrequency.ChooseDates

class ChooseDatesViewSpec extends ViewBaseSpec {

  val chooseDatesView: ChooseDates = injector.instanceOf[ChooseDates]

  "Rendering the Choose dates page with no errors" should {

    val form: Form[ReturnDatesModel] = ChooseDatesForm.datesForm

    lazy val view = chooseDatesView(form,Jan)(user, messages, mockAppConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title of '${viewMessages.ChoosePage.title}'" in {
      document.title shouldBe viewMessages.ChoosePage.title
    }

    s"have the correct page heading of '${viewMessages.ChoosePage.heading}'" in {
      elementText("h1") shouldBe viewMessages.ChoosePage.heading
    }

    "should not display an error" in {
      document.select("#error-summary-display").isEmpty shouldBe true
    }

    s"have the correct current return dates of '${viewMessages.ChoosePage.question} ${viewMessages.option1Jan}'" in {
      elementText("#period-option-hint") shouldBe viewMessages.ChoosePage.question + viewMessages.option1Jan + viewMessages.fullStop
    }

    "have the correct options for return dates of" in {
      elementText(".govuk-radios > div:nth-child(1) > label") shouldBe viewMessages.option2Feb
      elementText(".govuk-radios > div:nth-child(2) > label") shouldBe viewMessages.option3Mar
      elementText(".govuk-radios > div:nth-child(3) > label") shouldBe viewMessages.option4Monthly
    }

    "have the correct hint text of" in {
      elementText(".govuk-radios__item:nth-child(1) > div > p") shouldBe viewMessages.changeEndOfQuarter
      elementText(".govuk-radios__item:nth-child(2) > div > p") shouldBe viewMessages.changeEndOfQuarter
      elementText(".govuk-radios__item:nth-child(3) > div > p") shouldBe viewMessages.changeEndOfMonth
    }

    s"have a continue button with the text '${viewMessages.continue}'" in {
      elementText("button[type=\"submit\"]") shouldBe viewMessages.continue
    }

    s"have a back link with the correct text and url '${viewMessages.back}'" in {
      elementText(".govuk-back-link") shouldBe viewMessages.back
      element(".govuk-back-link").attr("href") shouldBe mockAppConfig.manageVatUrl
    }
  }

  "Rendering the Choose Dates page for a Monthly Accounting user" should {

    val form: Form[ReturnDatesModel] = ChooseDatesForm.datesForm

    lazy val view = chooseDatesView(form, Monthly)(user, messages, mockAppConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title of '${viewMessages.ChoosePage.title}'" in {
      document.title shouldBe viewMessages.ChoosePage.title
    }

    s"have the correct page heading of '${viewMessages.ChoosePage.heading}'" in {
      elementText("h1") shouldBe viewMessages.ChoosePage.heading
    }

    "should not display an error" in {
      document.select("#error-summary-display").isEmpty shouldBe true
    }

    s"have the correct current return dates of '${viewMessages.ChoosePage.question} ${viewMessages.option4Monthly}'" in {
      elementText("#period-option-hint") shouldBe viewMessages.ChoosePage.question + viewMessages.option4Monthly + viewMessages.fullStop
    }

    "have the correct options for return dates of" in {
      elementText(".govuk-radios > div:nth-child(1) > label") shouldBe viewMessages.option1Jan
      elementText(".govuk-radios > div:nth-child(2) > label") shouldBe viewMessages.option2Feb
      elementText(".govuk-radios > div:nth-child(3) > label") shouldBe viewMessages.option3Mar
    }

    "have the correct hint text of" in {
      elementText(".govuk-radios__item:nth-child(1) > div > p") shouldBe viewMessages.changeEndOfMonth
      elementText(".govuk-radios__item:nth-child(2) > div > p") shouldBe viewMessages.changeEndOfMonth
      elementText(".govuk-radios__item:nth-child(3) > div > p") shouldBe viewMessages.changeEndOfMonth
    }

    s"have a continue button with the text '${viewMessages.continue}'" in {
      elementText("button[type=\"submit\"]") shouldBe viewMessages.continue
    }

    s"have the back link with correct text and url '${viewMessages.back}'" in {
      elementText(".govuk-back-link") shouldBe viewMessages.back
      element(".govuk-back-link").attr("href") shouldBe mockAppConfig.manageVatUrl
    }
  }

  "Rendering the Choose Dates page for an Annual Accounting user" should {

    val form: Form[ReturnDatesModel] = ChooseDatesForm.datesForm

    lazy val view = chooseDatesView(form, Annually)(user, messages, mockAppConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title of '${viewMessages.ChoosePage.title}'" in {
      document.title shouldBe viewMessages.ChoosePage.title
    }

    s"have the correct page heading of '${viewMessages.ChoosePage.heading}'" in {
      elementText("h1") shouldBe viewMessages.ChoosePage.heading
    }

    "should not display an error" in {
      document.select("#error-summary-display").isEmpty shouldBe true
    }

    s"have the correct current return dates of '${viewMessages.annually}'" in {
      elementText("#period-option-hint") shouldBe viewMessages.annually
    }

    "have the correct options for return dates of" in {
      elementText(".govuk-radios > div:nth-child(1) > label") shouldBe viewMessages.option1Jan
      elementText(".govuk-radios > div:nth-child(2) > label") shouldBe viewMessages.option2Feb
      elementText(".govuk-radios > div:nth-child(3) > label") shouldBe viewMessages.option3Mar
      elementText(".govuk-radios > div:nth-child(4) > label") shouldBe viewMessages.option4Monthly
    }

    s"have a continue button with the text '${viewMessages.continue}'" in {
      elementText("button[type=\"submit\"]") shouldBe viewMessages.continue
    }

    s"have the back link with correct text and url '${viewMessages.back}'" in {
      elementText(".govuk-back-link") shouldBe viewMessages.back
      element(".govuk-back-link").attr("href") shouldBe mockAppConfig.manageVatUrl
    }
  }

  "Rendering the Choose dates page with errors" should {

    val form: Form[ReturnDatesModel] = ChooseDatesForm.datesForm.bind(Map("period-option" -> ""))

    lazy val view = chooseDatesView(form,Monthly)(user, messages, mockAppConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title of '${viewMessages.ChoosePage.errorTitle}'" in {
      document.title shouldBe viewMessages.ChoosePage.errorTitle
    }

    s"have the correct page heading of '${viewMessages.ChoosePage.heading}'" in {
      elementText("h1") shouldBe viewMessages.ChoosePage.heading
    }

    "should display an error" in {
      elementText(".govuk-error-summary") shouldBe s"${viewMessages.errorHeading} ${viewMessages.ChoosePage.error}"
    }

    s"have the correct current return dates of '${viewMessages.ChoosePage.question} ${viewMessages.option4Monthly}'" in {
      elementText("#period-option-hint") shouldBe viewMessages.ChoosePage.question + viewMessages.option4Monthly + viewMessages.fullStop
    }

    "have the correct options for return dates of" in {
      elementText(".govuk-radios > div:nth-child(1) > label") shouldBe viewMessages.option1Jan
      elementText(".govuk-radios > div:nth-child(2) > label") shouldBe viewMessages.option2Feb
      elementText(".govuk-radios > div:nth-child(3) > label") shouldBe viewMessages.option3Mar
    }
  }
}
