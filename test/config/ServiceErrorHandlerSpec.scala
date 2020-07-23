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

package config

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.ErrorTemplate

class ServiceErrorHandlerSpec extends ViewBaseSpec {

  val errorTemplate: ErrorTemplate = injector.instanceOf[ErrorTemplate]

  val service: ServiceErrorHandler = new ServiceErrorHandler(messagesApi, mockAppConfig, errorTemplate)

  object Selectors {
    val pageHeading = "h1"
    val message = "#content > p:nth-child(3)"
  }

  "The not found template" should {

    lazy val view = service.notFoundTemplate
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "display the correct title" in {
      document.title shouldBe "Page not found - VAT - GOV.UK"
    }

    "displays the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "This page cannot be found"
    }

    "displays the correct message" in {
      elementText(Selectors.message) shouldBe "Please check that you have entered the correct web address."
    }
  }

  "The internal server error template" should {

    lazy val view = service.internalServerErrorTemplate
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "display the correct title" in {
      document.title shouldBe "There is a problem with the service - VAT - GOV.UK"
    }

    "displays the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Sorry, there is a problem with the service"
    }

    "displays the correct message" in {
      elementText(Selectors.message) shouldBe "Try again later."
    }
  }
}
