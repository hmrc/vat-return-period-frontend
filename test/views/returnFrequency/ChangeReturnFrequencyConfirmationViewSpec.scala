/*
 * Copyright 2021 HM Revenue & Customs
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
import views.html.returnFrequency.{ChangeReturnFrequencyConfirmation => CRFCView}

class ChangeReturnFrequencyConfirmationViewSpec extends ViewBaseSpec {

  val changeReturnFrequencyConfirmationView: CRFCView =
    injector.instanceOf[CRFCView]


  "Rendering the Dates Received page for an individual" when {

    "contactPref is 'DIGITAL'" when {

      "emailVerified is true" should {

        lazy val view =
          changeReturnFrequencyConfirmationView(contactPref = Some("DIGITAL"), emailVerified = true)(user, messages, mockAppConfig)
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

        s"have the correct p1 of '${viewMessages.ReceivedPage.digiPrefWithEmail}'" in {
          paragraph(1) shouldBe viewMessages.ReceivedPage.digiPrefWithEmail
        }

        s"have the correct p2 of '${viewMessages.ReceivedPage.newDates}'" in {
          paragraph(2) shouldBe viewMessages.ReceivedPage.newDates
        }

        "not have a link to change client" in {
          elementExtinct("#change-client-text")
        }

        "have the correct finish button" which {

          s"has the text '${viewMessages.finish}'" in {
            elementText(".govuk-button") shouldBe viewMessages.finish
          }

          "has link back to customer details page" in {
            element(".govuk-button").attr("href") shouldBe mockAppConfig.manageVatUrl
          }
        }
      }

      "emailVerified is false" should {

        lazy val view = changeReturnFrequencyConfirmationView(contactPref = Some("DIGITAL"))(user, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the correct p1 of '${viewMessages.ReceivedPage.digitalPref}'" in {
          paragraph(1) shouldBe viewMessages.ReceivedPage.digitalPref
        }
      }
    }

    "contactPref is 'PAPER'" should {

      lazy val view = changeReturnFrequencyConfirmationView(contactPref = Some("PAPER"))(user, messages, mockAppConfig)
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

      s"have the correct p1 of '${viewMessages.ReceivedPage.paperPref}'" in {
        paragraph(1) shouldBe viewMessages.ReceivedPage.paperPref
      }

      s"have the correct p2 of '${viewMessages.ReceivedPage.newDates}'" in {
        paragraph(2) shouldBe viewMessages.ReceivedPage.newDates
      }

      "not have a link to change client" in {
        elementExtinct("#change-client-text")
      }

      "have the correct finish button" which {

        s"has the text '${viewMessages.finish}'" in {
          elementText(".govuk-button") shouldBe viewMessages.finish
        }

        "has link back to customer details page" in {
          element(".govuk-button").attr("href") shouldBe mockAppConfig.manageVatUrl
        }
      }
    }

    "no contact details are retrieved" should {

      lazy val view = changeReturnFrequencyConfirmationView()(user, messages, mockAppConfig)
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

      s"have the correct p1 of '${viewMessages.ReceivedPage.contactPrefError}'" in {
        paragraph(1) shouldBe viewMessages.ReceivedPage.contactPrefError
      }

      s"have the correct p2 of '${viewMessages.ReceivedPage.newDates}'" in {
        paragraph(2) shouldBe viewMessages.ReceivedPage.newDates
      }

      "not have a link to change client" in {
        elementExtinct("#change-client-text")
      }

      "have the correct finish button" which {

        s"has the text '${viewMessages.backToClient}'" in {
          elementText(".govuk-button") shouldBe viewMessages.finish
        }

        "has link back to customer details page" in {
          element(".govuk-button").attr("href") shouldBe mockAppConfig.manageVatUrl
        }
      }
    }

  }

  "Rendering the Dates Received page for an agent without bulk paper content" when {

    "they have selected to receive email notifications" when {

      "there is a client name and the changeClientFeature is on" should {

        lazy val view = {
          changeReturnFrequencyConfirmationView(
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

        s"have the correct p1 of '${viewMessages.ReceivedPage.p1AgentBulk}'" in {
          paragraph(1) shouldBe viewMessages.ReceivedPage.p1AgentBulk
        }

        s"have the correct p2 of '${viewMessages.ReceivedPage.p2Agent}'" in {
          paragraph(2) shouldBe viewMessages.ReceivedPage.p2Agent
        }

        s"have the correct p3 of '${viewMessages.ReceivedPage.newDates}'" in {
          paragraph(3) shouldBe viewMessages.ReceivedPage.newDates
        }

        "have the correct back to client's details button" which {

          s"has the text '${viewMessages.backToClient}'" in {
            elementText(".govuk-button") shouldBe viewMessages.backToClient
          }

          "has link back to customer details page" in {
            element(".govuk-button").attr("href") shouldBe mockAppConfig.manageVatUrl
          }
        }

        "there is no client name" should {

          lazy val view = changeReturnFrequencyConfirmationView(
            agentEmail = Some(agentEmail))(agentUser, messages, mockAppConfig)
          lazy implicit val document: Document = Jsoup.parse(view.body)

          s"have the correct p2 of '${viewMessages.ReceivedPage.p2AgentNoClientName}'" in {
            paragraph(2) shouldBe viewMessages.ReceivedPage.p2AgentNoClientName
          }
        }

        "they have selected to not receive email notifications" when {

          "there is a client name" should {

            lazy val view = changeReturnFrequencyConfirmationView(
              clientName = Some("MyCompany Ltd"))(agentUser, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)

            s"have the correct p1 of '${viewMessages.ReceivedPage.p2Agent}" in {
              paragraph(1) shouldBe viewMessages.ReceivedPage.p2Agent
            }

            s"have the correct p2 of '${viewMessages.ReceivedPage.newDates}'" in {
              paragraph(2) shouldBe viewMessages.ReceivedPage.newDates
            }
          }

          "there is no client name" should {

            lazy val view = changeReturnFrequencyConfirmationView()(agentUser, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)

            s"have the correct p1 of '${viewMessages.ReceivedPage.p2AgentNoClientName}" in {
              paragraph(1) shouldBe viewMessages.ReceivedPage.p2AgentNoClientName
            }

            s"have the correct p2 of '${viewMessages.ReceivedPage.newDates}'" in {
              paragraph(2) shouldBe viewMessages.ReceivedPage.newDates
            }
          }
        }
      }
    }
  }

  "Rendering the Dates Received page for an agent with bulk paper content" when {

    "they have selected to receive email notifications" when {

      "there is a client name and the changeClientFeature is on" should {

        lazy val view = {
          changeReturnFrequencyConfirmationView(
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

        s"have the correct p1 of '${viewMessages.ReceivedPage.p1AgentBulk}'" in {
          paragraph(1) shouldBe viewMessages.ReceivedPage.p1AgentBulk
        }

        s"have the correct p2 of '${viewMessages.ReceivedPage.p2Agent}'" in {
          paragraph(2) shouldBe viewMessages.ReceivedPage.p2Agent
        }

        s"have the correct p3 of '${viewMessages.ReceivedPage.newDates}'" in {
          paragraph(3) shouldBe viewMessages.ReceivedPage.newDates
        }

        "have the correct f button" which {

          s"has the text '${viewMessages.backToClient}'" in {
            elementText(".govuk-button") shouldBe viewMessages.backToClient
          }

          "has link back to customer details page" in {
            element(".govuk-button").attr("href") shouldBe mockAppConfig.manageVatUrl
          }
        }

        "there is no client name" should {

          lazy val view = changeReturnFrequencyConfirmationView(
            agentEmail = Some(agentEmail))(agentUser, messages, mockAppConfig)
          lazy implicit val document: Document = Jsoup.parse(view.body)

          s"have the correct p2 of '${viewMessages.ReceivedPage.p2AgentNoClientName}'" in {
            paragraph(2) shouldBe viewMessages.ReceivedPage.p2AgentNoClientName
          }
        }

        "they have selected to not receive email notifications" when {

          "there is a client name" should {

            lazy val view = changeReturnFrequencyConfirmationView(
              clientName = Some("MyCompany Ltd"))(agentUser, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)

            s"have the correct p1 of '${viewMessages.ReceivedPage.p2Agent}" in {
              paragraph(1) shouldBe viewMessages.ReceivedPage.p2Agent
            }

            s"have the correct p2 of '${viewMessages.ReceivedPage.newDates}'" in {
              paragraph(2) shouldBe viewMessages.ReceivedPage.newDates
            }
          }

          "there is no client name" should {

            lazy val view = changeReturnFrequencyConfirmationView()(agentUser, messages, mockAppConfig)
            lazy implicit val document: Document = Jsoup.parse(view.body)

            s"have the correct p1 of '${viewMessages.ReceivedPage.p2AgentNoClientName}'" in {
              paragraph(1) shouldBe viewMessages.ReceivedPage.p2AgentNoClientName
            }
          }
        }
      }
    }
  }
}