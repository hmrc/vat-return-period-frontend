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

import config.features.Features
import javax.inject.{Inject, Singleton}
import play.api.i18n.Lang
import play.api.mvc.Call
import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait AppConfig {
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val betaFeedbackUrl: String
  val betaFeedbackUnauthenticatedUrl: String
  val features: Features
  val accessibilityReportUrl: String
  val signInUrl: String
  val timeoutPeriod: Int
  val timeoutCountdown: Int
  def signOutUrl(identifier: String): String
  def exitSurveyUrl(identifier: String): String
  val unauthorisedSignOutUrl: String
  val agentClientLookupStartUrl: String => String
  val agentClientUnauthorisedUrl: String => String
  val changeClientUrl: String
  val agentActionUrl: String
  val govUkGuidanceMtdVat: String
  val govUkGuidanceAgentServices: String
  val manageVatUrl: String
  def languageMap: Map[String,Lang]
  val routeToSwitchLanguage: String => Call
  val vatSubscriptionBaseURL: String
  val vatSubscriptionDynamicStubURL: String
  val contactPreferenceURL: String
  val gtmContainer: String
}

@Singleton
class FrontendAppConfig @Inject()(environment: Environment, implicit val runModeConfiguration: Configuration,
                                  servicesConfig: ServicesConfig) extends AppConfig {
  protected def mode: Mode = environment.mode

  lazy val appName: String = servicesConfig.getString(ConfigKeys.appName)
  private lazy val platformHost = servicesConfig.getString(ConfigKeys.platformHost)

  override val vatSubscriptionBaseURL: String = servicesConfig.baseUrl("vat-subscription")
  override val vatSubscriptionDynamicStubURL: String = servicesConfig.baseUrl("vat-subscription-dynamic-stub")
  override val contactPreferenceURL: String = servicesConfig.baseUrl("contact-preferences")

  // Contact frontend
  private lazy val contactHost = servicesConfig.getString(ConfigKeys.contactFrontendService)
  private val contactFormServiceIdentifier = "VATC"

  // Feedback
  lazy val reportAProblemPartialUrl: String = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl: String = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  override lazy val betaFeedbackUrl: String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier" +
    s"&backUrl=${SafeRedirectUrl(manageVatUrl).encodedUrl}"

  override lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  // Gov.uk guidance
  override lazy val govUkGuidanceMtdVat: String = servicesConfig.getString(ConfigKeys.govUkGuidanceMtdVat)
  override lazy val govUkGuidanceAgentServices: String = servicesConfig.getString(ConfigKeys.govUkGuidanceAgentServices)

  //Time-out
  override lazy val timeoutPeriod: Int = servicesConfig.getInt(ConfigKeys.timeoutPeriod)
  override lazy val timeoutCountdown: Int = servicesConfig.getInt(ConfigKeys.timeoutCountDown)

  // Sign-in
  private lazy val signInBaseUrl: String = servicesConfig.getString(ConfigKeys.signInBaseUrl)
  private lazy val signInContinueBaseUrl: String = servicesConfig.getString(ConfigKeys.signInContinueBaseUrl)
  private lazy val signInContinueUrl: String = signInContinueBaseUrl + servicesConfig.getString(ConfigKeys.signInContinueUrl)
  private lazy val signInOrigin = servicesConfig.getString(ConfigKeys.appName)
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  // Sign-out
  private lazy val feedbackSurveyBaseUrl = servicesConfig.getString(ConfigKeys.feedbackSurveyHost) +
    servicesConfig.getString(ConfigKeys.feedbackSurveyUrl)
  override def exitSurveyUrl(identifier: String): String = s"$feedbackSurveyBaseUrl/$identifier"
  private lazy val governmentGatewayHost: String = servicesConfig.getString(ConfigKeys.governmentGatewayHost)
  override lazy val unauthorisedSignOutUrl: String = s"$governmentGatewayHost/bas-gateway/sign-out-without-state?continue=$signInContinueUrl"
  override def signOutUrl(identifier: String): String =
    s"$governmentGatewayHost/bas-gateway/sign-out-without-state?continue=${exitSurveyUrl(identifier)}"

  // Agent Client Lookup
  private lazy val agentClientLookupRedirectUrl: String => String = uri => SafeRedirectUrl(platformHost + uri).encodedUrl
  private lazy val agentClientLookupHost: String = servicesConfig.getString(ConfigKeys.vatAgentClientLookupFrontendHost)

  override lazy val agentClientLookupStartUrl: String => String = uri =>
    if(features.stubAgentClientLookup()) {
      testOnly.controllers.routes.StubAgentClientLookupController.show(uri).url
    } else {
      agentClientLookupHost +
        servicesConfig.getString(ConfigKeys.vatAgentClientLookupFrontendStartUrl) +
        s"?redirectUrl=${agentClientLookupRedirectUrl(uri)}"
    }

  override lazy val agentClientUnauthorisedUrl: String => String = uri =>
    if(features.stubAgentClientLookup()) {
      testOnly.controllers.routes.StubAgentClientLookupController.unauth(uri).url
    } else {
      agentClientLookupHost +
        servicesConfig.getString(ConfigKeys.vatAgentClientLookupFrontendUnauthorisedUrl) +
        s"?redirectUrl=${agentClientLookupRedirectUrl(uri)}"
    }

  override lazy val changeClientUrl: String = agentClientLookupHost +
    servicesConfig.getString(ConfigKeys.vatAgentClientLookupFrontendStartUrl)
  override lazy val agentActionUrl: String = agentClientLookupHost +
    servicesConfig.getString(ConfigKeys.vatAgentClientLookupFrontendAgentActionUrl)

  //Accessibility statement
  private lazy val accessibilityReportHost: String = servicesConfig.getString(ConfigKeys.accessibilityReportHost)
  override lazy val accessibilityReportUrl: String = accessibilityReportHost +
    servicesConfig.getString(ConfigKeys.accessibilityReportUrl)

  //Features
  override val features: Features = new Features

  // Manage VAT Subscription Frontend
  override lazy val manageVatUrl: String = servicesConfig.getString(ConfigKeys.manageVatHost) +
    servicesConfig.getString(ConfigKeys.manageVatUrl)

  override def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  override val routeToSwitchLanguage: String => Call = (lang: String) =>
    controllers.routes.LanguageController.switchToLanguage(lang)

  override val gtmContainer: String = servicesConfig.getString(ConfigKeys.gtmContainer)
}
