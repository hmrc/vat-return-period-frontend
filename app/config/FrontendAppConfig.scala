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

package config

import java.util.Base64

import common.ConfigKeys
import config.features.Features
import javax.inject.{Inject, Singleton}
import play.api.Mode.Mode
import play.api.mvc.Call
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig extends ServicesConfig {
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val betaFeedbackUrl: String
  val betaFeedbackUnauthenticatedUrl: String
  val whitelistedIps: Seq[String]
  val whitelistEnabled: Boolean
  val whitelistExcludedPaths: Seq[Call]
  val whitelistShutterPage: String

  val features: Features
  val accessibilityReportUrl : String
  val vatReturnPeriodUrl: String
}

@Singleton
class FrontendAppConfig @Inject()(environment: Environment,implicit val runModeConfiguration: Configuration) extends AppConfig {
  override protected def mode: Mode = environment.mode

  lazy val appName: String = getString(ConfigKeys.appName)

  private val contactHost = getString(ConfigKeys.contactFrontendService)
  private val contactFormServiceIdentifier = "VATC"

  override lazy val analyticsToken: String = getString(ConfigKeys.googleAnalyticsToken)
  override lazy val analyticsHost: String = getString(ConfigKeys.googleAnalyticsHost)
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  override lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  override lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  // Whitelist config
  private def whitelistConfig(key: String): Seq[String] = Some(new String(Base64.getDecoder
    .decode(getString(key)), "UTF-8"))
    .map(_.split(",")).getOrElse(Array.empty).toSeq

  override lazy val whitelistEnabled: Boolean = getBoolean(ConfigKeys.whitelistEnabled)
  override lazy val whitelistedIps: Seq[String] = whitelistConfig(ConfigKeys.whitelistedIps)
  override lazy val whitelistExcludedPaths: Seq[Call] = whitelistConfig(ConfigKeys.whitelistExcludedPaths) map
    (path => Call("GET", path))
  override val whitelistShutterPage: String = getString(ConfigKeys.whitelistShutterPage)

  override lazy val vatReturnPeriodUrl: String = baseUrl(ConfigKeys.vatReturnPeriodUrl)

  private lazy val accessibilityReportHost : String = getString(ConfigKeys.accessibilityReportHost)
  override val accessibilityReportUrl : String = accessibilityReportHost + getString(ConfigKeys.accessibilityReportUrl)
  override val features: Features = new Features
}
