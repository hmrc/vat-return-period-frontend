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

package views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.GovUkWrapper

class GovUkWrapperSpec extends ViewBaseSpec {

  val govUkWrapperView: GovUkWrapper = injector.instanceOf[GovUkWrapper]

  "creating a page with a footer" should {

    lazy val view = govUkWrapperView(mockAppConfig, "title")(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "not display a logo" in {
      document.select(".organisation-logo") shouldBe empty
    }

    "render the accessibility text in the footer" in {
      elementText("#footer > div > div > div.footer-meta-inner > ul > li:nth-child(2) > a") shouldBe "Accessibility statement"
    }

    "render the report link with the correct url" in {
      element("#footer > div > div > div.footer-meta-inner > ul > li:nth-child(2) > a").attr("href") shouldBe "/vat-through-software/accessibility-statement"
    }

    "display a sign out link which" should {

      "contain the text 'Sign out'" in {
        elementText("#proposition-links > li > a") shouldBe "Sign out"
      }

      s"link to ${controllers.routes.SignOutController.signOut(feedbackOnSignOut = true).url}" in {
        element("#proposition-links > li > a").attr("href") shouldBe controllers.routes.SignOutController.signOut(feedbackOnSignOut = true).url
      }
    }

    "contain a BETA banner which" should {

      "have the correct text" in {
        elementText(".beta-banner") shouldBe "BETA This is a new service – your feedback will help us to improve it."
      }

      "have a link to BETA feedback page" in {
        element(".beta-banner a").attr("href") shouldBe mockAppConfig.betaFeedbackUrl
      }
    }
  }
}
