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

package base


import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import common.SessionKeys
import common.SessionKeys.insolventWithoutAccessKey
import config.ServiceErrorHandler
import mocks.MockConfig
import models.auth.User
import org.jsoup.Jsoup
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext

trait BaseSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with MockFactory with UnitSpec with BeforeAndAfterEach {

  lazy val injector: Injector = app.injector

  implicit val config: Configuration = app.configuration

  implicit lazy val mockAppConfig: MockConfig = new MockConfig

  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession(insolventWithoutAccessKey -> "false")
  lazy val fakeRequestWithClientsVRN: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession(SessionKeys.CLIENT_VRN -> vrn, insolventWithoutAccessKey -> "false")
  lazy val insolventRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession(SessionKeys.insolventWithoutAccessKey -> "true")

  val vrn: String = "999999999"
  val arn = "ABCD12345678901"

  implicit lazy val messages: Messages = messagesApi.preferred(fakeRequest)

  lazy val errorHandler: ServiceErrorHandler = injector.instanceOf[ServiceErrorHandler]

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
  implicit lazy val mcc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  lazy val user: User[AnyContentAsEmpty.type] = User[AnyContentAsEmpty.type](vrn)(fakeRequest)
  lazy val agentUser: User[AnyContentAsEmpty.type] = User[AnyContentAsEmpty.type](vrn, true, Some(arn))(fakeRequestWithClientsVRN)

  def formatHtml(body: Html): String = Jsoup.parseBodyFragment(s"\n$body\n").toString.trim

}
