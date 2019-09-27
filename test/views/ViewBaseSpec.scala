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
import org.jsoup.nodes.{Document, Element}
import org.scalatest.Assertion
import play.twirl.api.Html

trait ViewBaseSpec extends BaseSpec {

  def element(cssSelector: String)(implicit document: Document): Element = {
    val elements = document.select(cssSelector)

    if (elements.size == 0) {
      fail(s"No element exists with the selector '$cssSelector'")
    }

    document.select(cssSelector).first()
  }

  def elementExtinct(cssSelector: String)(implicit document: Document): Assertion = {
    val elements = document.select(cssSelector)

    if (elements.size == 0) {
      succeed
    } else {
      fail(s"Element with selector '$cssSelector' was found!")
    }
  }

  def elementText(selector: String)(implicit document: Document): String = element(selector).text()

  def paragraph(index: Int)(implicit document: Document): String = elementText(s"article > p:nth-of-type($index)")

}
