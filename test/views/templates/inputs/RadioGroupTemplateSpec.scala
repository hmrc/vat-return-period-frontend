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

package views.templates.inputs

import models.returnFrequency.{Jan, Monthly}
import play.api.data.{Field, FormError}
import play.twirl.api.Html
import testOnly.forms.TextInputForm
import views.ViewBaseSpec
import views.html.templates.inputs.RadioGroup

class RadioGroupTemplateSpec extends ViewBaseSpec {

  val fieldName = "fieldName"
  val labelText = "labelText"
  val hintText = "hintText"
  val returnPeriodMonthly = Monthly
  val returnPeriodQuarterly = Jan
  val errorMessage = "error message"
  val choices: Seq[(String, String)] = Seq(
    "value1" -> "display1",
    "value2" -> "display2",
    "value3" -> "display3",
    "value4" -> "display4",
    "value5" -> "display5"
  )
  val radioGroup: RadioGroup = injector.instanceOf[RadioGroup]
  val changeEndOfMonth = "We’ll change your dates at the end of this month"
  val changeEndOfQuarter = "We’ll change your dates at the end of this quarter"

  def generateExpectedRadioMarkup(value: String, display: String, hiddenContent: String = changeEndOfMonth, checked: Boolean = false): String = {
    val jsHidden = if (!checked) " js-hidden" else ""
    s"""
       |  <div class="multiple-choice" data-target="hiddenContent-$fieldName-$value">
       |    <input type="radio" id="$fieldName-$value" name="$fieldName" value="$value"${if (checked) " checked" else ""}>
       |    <label for="$fieldName-$value">$display</label>
       |  </div>
       |  <div class="form-group panel panel-border-narrow form-hint$jsHidden" id="hiddenContent-$fieldName-$value">
       |    $hiddenContent
       |  </div>
      """.stripMargin
  }

  "Calling the radio helper with no choice pre-selected" when {

    "current return period is monthly" should {

      "render the choices as radio buttons" in {
        val field: Field = Field(TextInputForm.form, fieldName, Seq(), None, Seq(), None)
        val expectedMarkup = Html(
          s"""
             |<div class="form-group" id="period-option">
             | <fieldset aria-describedby="form-hint">
	         |   <div class="form-field">
             |        <legend>
             |          <h1 id="page-heading" class="heading-large">$labelText</h1>
             |        </legend>
             |
             |      ${generateExpectedRadioMarkup("value1", "display1")}
             |      ${generateExpectedRadioMarkup("value2", "display2")}
             |      ${generateExpectedRadioMarkup("value3", "display3")}
             |      ${generateExpectedRadioMarkup("value4", "display4")}
             |      ${generateExpectedRadioMarkup("value5", "display5")}
             |  </div>
             | </fieldset>
             |</div>
          """.stripMargin
        )

        val markup = radioGroup(field, choices, labelText, returnPeriodMonthly, None)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "current return period is quarterly" should {

      "render the choices as radio buttons" in {
        val field: Field = Field(TextInputForm.form, fieldName, Seq(), None, Seq(), None)
        val expectedMarkup = Html(
          s"""
             |<div class="form-group" id="period-option">
             | <fieldset aria-describedby="form-hint">
	          |   <div class="form-field">
             |        <legend>
             |          <h1 id="page-heading" class="heading-large">$labelText</h1>
             |        </legend>
             |
             |      ${generateExpectedRadioMarkup("value1", "display1", changeEndOfQuarter)}
             |      ${generateExpectedRadioMarkup("value2", "display2", changeEndOfQuarter)}
             |      ${generateExpectedRadioMarkup("value3", "display3", changeEndOfQuarter)}
             |      ${generateExpectedRadioMarkup("value4", "display4", changeEndOfQuarter)}
             |      ${generateExpectedRadioMarkup("value5", "display5", changeEndOfQuarter)}
             |  </div>
             | </fieldset>
             |</div>
          """.stripMargin
        )

        val markup = radioGroup(field, choices, labelText, returnPeriodQuarterly, None)
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }
  }

  "Calling the radio group helper with a choice pre-selected" should {

    "render a list of radio options with one pre-checked" in {
      val field: Field = Field(TextInputForm.form, fieldName, Seq(), None, Seq(), Some("value2"))
      val expectedMarkup = Html(
        s"""
           |<div class="form-group" id="period-option">
           |    <fieldset aria-describedby="form-hint">
	         |      <div class="form-field">
           |        <legend>
           |          <h1 id="page-heading" class="heading-large">$labelText</h1>
           |        </legend>
           |
           |      ${generateExpectedRadioMarkup("value1", "display1")}
           |      ${generateExpectedRadioMarkup("value2", "display2", checked = true)}
           |      ${generateExpectedRadioMarkup("value3", "display3")}
           |      ${generateExpectedRadioMarkup("value4", "display4")}
           |      ${generateExpectedRadioMarkup("value5", "display5")}
           |    </div>
           |   </fieldset>
           |</div>
        """.stripMargin
      )

      val markup = radioGroup(field, choices, labelText, returnPeriodMonthly, None)
      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }

  "Calling the radio group helper with an error" should {

    "render an error" in {
      val errorMessage = "Error message"
      val field: Field = Field(TextInputForm.form, fieldName, Seq(), None, Seq(FormError("text", errorMessage)), None)
      val expectedMarkup = Html(
        s"""
           |<div class="form-group" id="period-option">
           |    <fieldset aria-describedby="form-hint form-error">
           |      <div class="form-field--error panel-border-narrow">
           |        <legend>
           |          <h1 id="page-heading" class="heading-large">$labelText</h1>
           |        </legend>
           |
           |      <span id="form-error" class="error-message">
           |        <span class="visuallyhidden">Error:</span>
           |        $errorMessage
           |      </span>
           |
           |      ${generateExpectedRadioMarkup("value1", "display1")}
           |      ${generateExpectedRadioMarkup("value2", "display2")}
           |      ${generateExpectedRadioMarkup("value3", "display3")}
           |      ${generateExpectedRadioMarkup("value4", "display4")}
           |      ${generateExpectedRadioMarkup("value5", "display5")}
           |    </div>
           |   </fieldset>
           |</div>
        """.stripMargin
      )

      val markup = radioGroup(field, choices, labelText, returnPeriodMonthly, None)
      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }

  "Calling the radio helper with additional content" should {

    "render the choices as radio buttons with additional content" in {
      val additionalContent = Html("<p>Additional text</p>")
      val field: Field = Field(TextInputForm.form, fieldName, Seq(), None, Seq(), None)
      val expectedMarkup = Html(
        s"""
           |<div class="form-group" id="period-option">
           |  <fieldset aria-describedby="form-hint">
	         |    <div class="form-field">
           |        <legend>
           |          <h1 id="page-heading" class="heading-large">$labelText</h1>
           |        </legend>
           |
           |      $additionalContent
           |
           |      ${generateExpectedRadioMarkup("value1", "display1")}
           |      ${generateExpectedRadioMarkup("value2", "display2")}
           |      ${generateExpectedRadioMarkup("value3", "display3")}
           |      ${generateExpectedRadioMarkup("value4", "display4")}
           |      ${generateExpectedRadioMarkup("value5", "display5")}
           |
           |    </div>
           | </fieldset>
           |</div>
        """.stripMargin
      )

      val markup = radioGroup(field, choices, labelText, returnPeriodMonthly, Some(additionalContent))
      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }
}
