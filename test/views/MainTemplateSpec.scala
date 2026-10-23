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

package views

import config.ConfigKeys
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.hmrcfrontend.views.Aliases.UserResearchBanner
import views.helpers.UrBannerUtil
import views.html.MainTemplate

class MainTemplateSpec extends ViewBaseSpec {

  val injectedView: MainTemplate = injector.instanceOf[MainTemplate]
  val userBannerHeading: String = "Help make GOV.UK better"
  lazy val urBanner: UserResearchBanner = UrBannerUtil.getUrBanner()(mockAppConfig, messages)
  lazy val urBannerWithCloseButton: UserResearchBanner = UrBannerUtil.getUrBanner(hideCloseButton = false)(mockAppConfig, messages)

  override def afterEach(): Unit = {
    sys.props -= ConfigKeys.showUserResearchBannerFeature
    super.afterEach()
  }

  object Selectors {
    val serviceNameSelector = ".govuk-service-navigation__link"
    val serviceNameNoUserTypeSelector = ".govuk-service-navigation__text"
    val userResearchBanner = ".hmrc-user-research-banner"
    val userResearchBannerHeading = ".hmrc-user-research-banner__title"
    val userResearchBannerCloseButton = ".hmrc-user-research-banner__close"
  }

  "The MainTemplate" when {

    "the user is an agent" should {

      lazy val view = injectedView("", None, user = Some(agentUser))(Html(""))(agentUser, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the correct dynamic service name" in {
        elementText(Selectors.serviceNameSelector) shouldBe "Your client’s VAT details"
      }

      "have the correct header URL" in {
        element(Selectors.serviceNameSelector).attr("href") shouldBe mockAppConfig.agentClientLookupUrl
      }

    }

    "the user is not an agent" should {

      lazy val view = injectedView("", None, user = Some(user))(Html(""))(user, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the correct dynamic service name" in {
        elementText(Selectors.serviceNameSelector) shouldBe "Manage your VAT account"
      }

      "have the correct header URL" in {
        element(Selectors.serviceNameSelector).attr("href") shouldBe mockAppConfig.vatDetailsUrl
      }

    }

    "the user type cannot be determined" should {

      lazy val view = injectedView("", None)(Html(""))(fakeRequest, messages, mockAppConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the correct dynamic service name" in {
        elementText(Selectors.serviceNameNoUserTypeSelector) shouldBe "VAT"
      }

      "have the correct header URL" in {
        element(Selectors.serviceNameNoUserTypeSelector).attr("href") shouldBe ""
      }

    }

    "the showUserResearchBanner feature switch is turned off" when {

      "a user research banner is provided" should {

        lazy val view = injectedView("", None, user = Some(user), urBanner = Some(urBannerWithCloseButton))(Html(""))(user, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "not display the user research banner" in {
          mockAppConfig.features.showUserResearchBanner(false)
          elementExtinct(Selectors.userResearchBanner)
        }
      }
    }

    "the showUserResearchBanner feature switch is turned on" when {

      "a user research banner with a close button is provided" should {

        lazy val view = injectedView("", None, user = Some(user), urBanner = Some(urBannerWithCloseButton))(Html(""))(user, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display the correct user research banner heading" in {
          mockAppConfig.features.showUserResearchBanner(true)
          elementText(Selectors.userResearchBannerHeading) shouldBe userBannerHeading
        }

        "display the user research banner link with the configured url" in {
          mockAppConfig.features.showUserResearchBanner(true)
          element(s"${Selectors.userResearchBanner} a").attr("href") shouldBe mockAppConfig.urBannerUrl
        }

        "display the close button" in {
          mockAppConfig.features.showUserResearchBanner(true)
          document.select(Selectors.userResearchBannerCloseButton).size shouldBe 1
        }
      }

      "a user research banner without a close button is provided" should {

        lazy val view = injectedView("", None, user = Some(user), urBanner = Some(urBanner))(Html(""))(user, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display the correct user research banner heading" in {
          mockAppConfig.features.showUserResearchBanner(true)
          elementText(Selectors.userResearchBannerHeading) shouldBe userBannerHeading
        }

        "not display the close button" in {
          mockAppConfig.features.showUserResearchBanner(true)
          elementExtinct(Selectors.userResearchBannerCloseButton)
        }
      }

      "no user research banner is provided" should {

        lazy val view = injectedView("", None, user = Some(user))(Html(""))(user, messages, mockAppConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "not display the user research banner" in {
          mockAppConfig.features.showUserResearchBanner(true)
          elementExtinct(Selectors.userResearchBanner)
        }
      }
    }
  }
}
