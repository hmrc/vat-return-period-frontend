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

package base

import config.AppConfig
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.HeaderNames
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.{Application, Environment, Mode}
import stubs.AuthStub
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import utils.WireMockHelper
import org.scalatest._
import play.api.libs.json.JsValue

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable}

trait BaseISpec extends WordSpec
  with WireMockHelper
  with Matchers
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with GuiceOneServerPerSuite
  with GivenWhenThen {

  def servicesConfig: Map[String, String] = Map(
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
    "microservice.services.vat-subscription.host" -> WireMockHelper.wireMockHost,
    "microservice.services.vat-subscription.port" -> WireMockHelper.wireMockPort.toString,
    "microservice.services.auth.host" -> WireMockHelper.wireMockHost,
    "microservice.services.auth.port" -> WireMockHelper.wireMockPort.toString,
    "microservice.services.contact-preferences.host" -> WireMockHelper.wireMockHost,
    "microservice.services.contact-preferences.port" -> WireMockHelper.wireMockPort.toString,
    "features.stubContactPreferences.enabled" -> "false"
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(servicesConfig)
    .build()

  lazy val httpClient: HttpClient = app.injector.instanceOf[HttpClient]
  lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]
  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  implicit lazy val messages: Messages = Messages(Lang("en-GB"), messagesApi)
  val appRouteContext: String = "/vat-through-software/account/returns"

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val vrn = "999999999"

  override def beforeAll(): Unit = {
    super.beforeAll()
    startServer()
  }

  override def afterAll(): Unit = {
    stopServer()
    super.afterAll()
  }

  class PreconditionBuilder {
    implicit val builder: PreconditionBuilder = this
    def user: User = new User()
    def agent: Agent = new Agent()
  }

  def given: PreconditionBuilder = new PreconditionBuilder

  class User()(implicit builder: PreconditionBuilder) {
    def isAuthenticated: PreconditionBuilder = {
      Given("I stub a User who successfully signed up to MTD VAT")
      AuthStub.authorised()
      builder
    }

    def isNotSignedUpToMtdVat: PreconditionBuilder = {
      Given("I stub a User who is not signed up to MTD VAT")
      AuthStub.unauthorised()
      builder
    }
  }

  class Agent()(implicit builder: PreconditionBuilder) {
    def isSignedUpToAgentServices: PreconditionBuilder = {
      Given("I stub an Agent successfully signed up to Agent Services")
      AuthStub.agentAuthorised()
      builder
    }

    def isNotSignedUpToAgentServices: PreconditionBuilder = {
      Given("I stub an Agent who is NOT signed up to Agent Services")
      AuthStub.agentUnauthorised()
      builder
    }
  }

  def await[T](awaitable: Awaitable[T]): T = Await.result(awaitable, Duration.Inf)

  def get(path: String, additionalCookies: Map[String, String] = Map.empty): WSResponse = await(
    buildRequest(path, additionalCookies).get()
  )

  def postJSValueBody(path: String, additionalCookies: Map[String, String] = Map.empty)(body: JsValue): WSResponse = await(
    buildRequest(path, additionalCookies).post(body)
  )

  def buildRequest(path: String, additionalCookies: Map[String, String] = Map.empty): WSRequest =
    wsClient.url(s"http://localhost:$port$appRouteContext$path")
      .withHeaders(HeaderNames.COOKIE -> SessionCookieBaker.bakeSessionCookie(additionalCookies), "Csrf-Token" -> "nocheck")
      .withFollowRedirects(false)

}
