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

package config.filters

import mocks.MockConfig
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Action, Call}
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, Configuration}


class WhitelistFilterSpec extends PlaySpec with GuiceOneAppPerSuite {

  lazy implicit val appConfig: Configuration = app.configuration

  lazy val mockConfig = new MockConfig()

  override implicit lazy val app: Application = {
    new GuiceApplicationBuilder()
      .configure(Configuration(
        "whitelist.enabled" -> true
      ))
      .routes({
        case ("GET", "/hello-world") => Action(Ok("success"))
        case _ => Action(Ok("failure"))
      })
      .build()
  }

  "Whitelist filter" when {
    "provided with a valid ip address" should {

      lazy val fakeRequest = FakeRequest("GET", "/hello-world").withHeaders(
        "True-Client-IP" -> "127.0.0.1"
      )

      lazy val Some(result) = route(app, fakeRequest)

      "return a 200" in {
        status(result) mustBe OK
      }

      "return a success" in {
        contentAsString(result) mustBe "success"
      }

    }

    "provided with an invalid ip address" should {

      lazy val fakeRequest = FakeRequest("GET", "/hello-world").withHeaders(
        "True-Client-IP" -> "127.0.0.2"
      )

      Call(fakeRequest.method, fakeRequest.uri)

      lazy val Some(result) = route(app, fakeRequest)

      "return a 303" in {
        status(result) mustBe SEE_OTHER
      }

      "redirect to shutter page" in {
        redirectLocation(result) mustBe Some(mockConfig.whitelistShutterPage)
      }
    }

  }

}
