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

package config

object ConfigKeys {

  val googleAnalyticsToken: String = "google-analytics.token"
  val googleAnalyticsHost: String = "google-analytics.host"

  val contactFrontendService: String = "contact-frontend.host"
  val appName: String = "appName"
  val platformHost: String = "platform.host"

  val stubAgentClientLookupFeature: String = "features.stubAgentClientLookup.enabled"

  val accessibilityReportUrl: String = "accessibilityReport.url"
  val accessibilityReportHost: String = "accessibilityReport.host"

  val signInBaseUrl: String = "signIn.url"
  val signInContinueBaseUrl: String = "signIn.continueBaseUrl"
  val signInContinueUrl: String = "signIn.continueUrl"

  val timeoutPeriod: String = "timeout.period"
  val timeoutCountDown: String = "timeout.countDown"

  val feedbackSurveyHost: String = "feedback-frontend.host"
  val feedbackSurveyUrl: String  = "feedback-frontend.url"
  val governmentGatewayHost: String = "government-gateway.host"

  val vatAgentClientLookupFrontendHost: String = "vat-agent-client-lookup-frontend.host"
  val vatAgentClientLookupFrontendStartUrl: String = "vat-agent-client-lookup-frontend.startUrl"
  val vatAgentClientLookupFrontendUnauthorisedUrl: String = "vat-agent-client-lookup-frontend.unauthorisedUrl"

  val govUkGuidanceMtdVat: String = "gov-uk.mtdVat"
  val govUkGuidanceAgentServices: String = "gov-uk.agentServices"

  val manageVatUrl: String = "manage-vat-subscription-frontend.url"
  val manageVatHost: String = "manage-vat-subscription-frontend.host"

  val gtmContainer: String = "tracking-consent-frontend.gtm.container"

  val footerCookiesUrl: String = "mdtp.cookies"
  val footerPrivacyUrl: String = "mdtp.privacyPolicy"
  val footerTermsConditionsUrl: String = "mdtp.termsAndConditions"
  val footerHelpUrl: String = "gov-uk.helpUsingGovuk"
}
