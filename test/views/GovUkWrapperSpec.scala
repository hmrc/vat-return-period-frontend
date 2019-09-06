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

package views

import base.BaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class GovUkWrapperSpec extends BaseSpec {

  "creating a page with a footer" should {
    lazy val view = views.html.govuk_wrapper(mockAppConfig, "title")(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the accessibility text in the footer" in {
      elementText("#footer > div > div > div.footer-meta-inner > ul > li:nth-child(2) > a") shouldBe "Accessibility"
    }
    "render the report link with the correct url" in {
      element("#footer > div > div > div.footer-meta-inner > ul > li:nth-child(2) > a").attr ("href") shouldBe "/vat-through-software/accessibility-statement"
    }
  }

}
