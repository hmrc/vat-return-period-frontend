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

package views.templates.inputs

import play.api.data.Field
import play.twirl.api.Html
import testOnly.forms.FeatureSwitchForm
import views.ViewBaseSpec

class SingeCheckBoxSpec extends ViewBaseSpec {

  "Rendering the single check box" when {

    val fieldName = "testFieldName"
    val label = "testLabel"

    "checkbox value is true" should {
      val value = "true"
      val field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(), Some(value))

      val expectedMarkup = Html(
        s"""
           |<div class="multiple-choice">
           |    <input id="$fieldName" name="$fieldName" type="checkbox" value="true" checked>
           |    <label for="$fieldName">$label</label>
           |</div>
           |""".stripMargin
      )

      val markup = views.html.templates.inputs.singleCheckbox(field, label)

      "render the expected markup" in {
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "checkbox value is false" should {
      val value = "false"
      val field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(), Some(value))

      val expectedMarkup = Html(
        s"""
           |<div class="multiple-choice">
           |    <input id="$fieldName" name="$fieldName" type="checkbox" value="true">
           |    <label for="$fieldName">$label</label>
           |</div>
           |""".stripMargin
      )

      val markup = views.html.templates.inputs.singleCheckbox(field, label)

      "render the expected markup" in {
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "checkbox value is blank or missing" should {
      val value = ""
      val field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(),Some(value))

      val expectedMarkup = Html(
        s"""
           |<div class="multiple-choice">
           |    <input id="$fieldName" name="$fieldName" type="checkbox" value="true">
           |    <label for="$fieldName">$label</label>
           |</div>
           |""".stripMargin
      )

      val markup = views.html.templates.inputs.singleCheckbox(field, label)

      "render the expected markup" in {
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }


  }
}