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

import assets.BaseTestConstants.agentEmail
import assets.messages.{ReturnFrequencyMessages => viewMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class ChangeReturnFrequencyConfirmationViewSpec extends ViewBaseSpec {

  "Rendering the Dates Received page for an individual" when {

    "contactPref is 'DIGITAL'" should {

      lazy val view = views.html.returnFrequency.change_return_frequency_confirmation(contactPref = Some("DIGITAL"))(user, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title of '${viewMessages.ReceivedPage.title}'" in {
        document.title shouldBe viewMessages.ReceivedPage.title
      }

      s"have a correct page heading of '${viewMessages.ReceivedPage.heading}'" in {
        elementText("#page-heading") shouldBe viewMessages.ReceivedPage.heading
      }

      s"have the correct h2 '${viewMessages.ReceivedPage.h2}'" in {
        elementText("h2") shouldBe viewMessages.ReceivedPage.h2
      }

      s"have the correct p1 of '${viewMessages.ReceivedPage.p1}'" in {
        paragraph(1) shouldBe viewMessages.ReceivedPage.digitalPref
      }

      s"have the correct p2 of '${viewMessages.ReceivedPage.p2}'" in {
        paragraph(2) shouldBe viewMessages.ReceivedPage.contactDetails
      }

      "not have a link to change client" in {
        elementExtinct("#change-client-text")
      }

      "have the correct finish button" which {

        s"has the text '${viewMessages.finish}'" in {
          elementText("#finish") shouldBe viewMessages.finish
        }

        "has link back to customer details page" in {
          element("#finish").attr("href") shouldBe mockAppConfig.manageVatUrl
        }
      }
    }

    "contactPref is 'PAPER'" should {

      lazy val view = views.html.returnFrequency.change_return_frequency_confirmation(contactPref = Some("PAPER"))(user, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title of '${viewMessages.ReceivedPage.title}'" in {
        document.title shouldBe viewMessages.ReceivedPage.title
      }

      s"have a correct page heading of '${viewMessages.ReceivedPage.heading}'" in {
        elementText("#page-heading") shouldBe viewMessages.ReceivedPage.heading
      }

      s"have the correct h2 '${viewMessages.ReceivedPage.h2}'" in {
        elementText("h2") shouldBe viewMessages.ReceivedPage.h2
      }

      s"have the correct p1 of '${viewMessages.ReceivedPage.p1}'" in {
        paragraph(1) shouldBe viewMessages.ReceivedPage.paperPref
      }

      s"have the correct p2 of '${viewMessages.ReceivedPage.p2}'" in {
        paragraph(2) shouldBe viewMessages.ReceivedPage.contactDetails
      }

      "not have a link to change client" in {
        elementExtinct("#change-client-text")
      }

      "have the correct finish button" which {

        s"has the text '${viewMessages.finish}'" in {
          elementText("#finish") shouldBe viewMessages.finish
        }

        "has link back to customer details page" in {
          element("#finish").attr("href") shouldBe mockAppConfig.manageVatUrl
        }
      }
    }

    "no contact details are retrieved" should {

      lazy val view = views.html.returnFrequency.change_return_frequency_confirmation()(user, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title of '${viewMessages.ReceivedPage.title}'" in {
        document.title shouldBe viewMessages.ReceivedPage.title
      }

      s"have a correct page heading of '${viewMessages.ReceivedPage.heading}'" in {
        elementText("#page-heading") shouldBe viewMessages.ReceivedPage.heading
      }

      s"have the correct h2 '${viewMessages.ReceivedPage.h2}'" in {
        elementText("h2") shouldBe viewMessages.ReceivedPage.h2
      }

      s"have the correct p1 of '${viewMessages.ReceivedPage.p1}'" in {
        paragraph(1) shouldBe viewMessages.ReceivedPage.contactPrefError
      }

      s"have the correct p2 of '${viewMessages.ReceivedPage.p2}'" in {
        paragraph(2) shouldBe viewMessages.ReceivedPage.contactDetails
      }

      "not have a link to change client" in {
        elementExtinct("#change-client-text")
      }

      "have the correct finish button" which {

        s"has the text '${viewMessages.finish}'" in {
          elementText("#finish") shouldBe viewMessages.finish
        }

        "has link back to customer details page" in {
          element("#finish").attr("href") shouldBe mockAppConfig.manageVatUrl
        }
      }
    }

  }

  "Rendering the Dates Received page for an agent" when {

    "they have selected to receive email notifications" when {

      "there is a client name and the changeClientFeature is on" should {

        lazy val view = {
          views.html.returnFrequency.change_return_frequency_confirmation(
            clientName = Some("MyCompany Ltd"), agentEmail = Some(agentEmail))(agentUser, messages, mockAppConfig)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the correct document title of '${viewMessages.ReceivedPage.titleAgent}'" in {
          document.title shouldBe viewMessages.ReceivedPage.titleAgent
        }

        s"have a correct page heading of '${viewMessages.ReceivedPage.heading}'" in {
          elementText("#page-heading") shouldBe viewMessages.ReceivedPage.heading
        }

        s"have the correct h2 '${viewMessages.ReceivedPage.h2}'" in {
          elementText("h2") shouldBe viewMessages.ReceivedPage.h2
        }

        s"have the correct p1 of '${viewMessages.ReceivedPage.p1Agent}'" in {
          paragraph(1) shouldBe viewMessages.ReceivedPage.p1Agent
        }

        s"have the correct p2 of '${viewMessages.ReceivedPage.p2Agent}'" in {
          paragraph(2) shouldBe viewMessages.ReceivedPage.p2Agent
        }

        "display the 'change another clients details' link" in {
          elementText("#change-client-text") shouldBe viewMessages.ReceivedPage.newChangeClientDetails
          element("#change-client-link").attr("href") shouldBe
            controllers.routes.ChangeClientController.changeClient().url
        }

        "have the correct finish button" which {

          s"has the text '${viewMessages.finish}'" in {
            elementText("#finish") shouldBe viewMessages.finish
          }

          "has link back to customer details page" in {
            element("#finish").attr("href") shouldBe mockAppConfig.manageVatUrl
          }
        }
      }

      "there is no client name" should {

        lazy val view = views.html.returnFrequency.change_return_frequency_confirmation(
          agentEmail = Some(agentEmail))(agentUser, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the correct p2 of '${viewMessages.ReceivedPage.p2AgentNoClientName}'" in {
          paragraph(2) shouldBe viewMessages.ReceivedPage.p2AgentNoClientName
        }
      }
    }

    "they have selected to not receive email notifications" when {

      "there is a client name" should {

        lazy val view = views.html.returnFrequency.change_return_frequency_confirmation(
          clientName = Some("MyCompany Ltd"))(agentUser, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the correct p1 of '${viewMessages.ReceivedPage.confirmationLetter}" in {
          paragraph(1) shouldBe viewMessages.ReceivedPage.confirmationLetter
        }

        s"have the correct p2 of '${viewMessages.ReceivedPage.p2Agent}'" in {
          paragraph(2) shouldBe viewMessages.ReceivedPage.p2Agent
        }
      }

      "there is no client name" should {

        lazy val view = views.html.returnFrequency.change_return_frequency_confirmation()(agentUser, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the correct p1 of '${viewMessages.ReceivedPage.confirmationLetter}" in {
          paragraph(1) shouldBe viewMessages.ReceivedPage.confirmationLetter
        }

        s"have the correct p2 of '${viewMessages.ReceivedPage.p2AgentNoClientName}'" in {
          paragraph(2) shouldBe viewMessages.ReceivedPage.p2AgentNoClientName
        }
      }
    }
  }
}
