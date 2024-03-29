/*
 * Copyright 2023 HM Revenue & Customs
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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.returnFrequency.SubscriptionUpdateResponseModel
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.Json
import utils.WireMockMethods

object ReturnFrequencyStub extends WireMockMethods {

  private val subscriptionUri: String => String = vrn => s"/vat-subscription/$vrn/return-period"

  def putSubscriptionSuccess(response: SubscriptionUpdateResponseModel): StubMapping = {
    when(method = PUT, uri = subscriptionUri("999999999"))
      .thenReturn(status = OK, body = Json.toJson(response))
  }

  def putSubscriptionError(): StubMapping = {
    when(method = PUT, uri = subscriptionUri("999999999"))
      .thenReturn(status = BAD_REQUEST, body = Json.obj("code" -> "Terry Bell Tings"))
  }
}
