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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.contactPreferences.ContactPreference
import play.api.http.Status.OK
import play.api.libs.json.{JsObject, Json}
import utils.WireMockMethods

object ContactPreferencesStub extends WireMockMethods {

  val uri: String => String = vrn => s"/contact-preferences/vat/vrn/$vrn"
  val digitalContactPreferenceModel = ContactPreference("DIGITAL")

  val digitalContactPreferenceJson: JsObject = Json.obj("preference" -> "digital")

  def stubGetContactPreference(vrn: String): StubMapping = {
    when(method = GET, uri = uri(vrn))
      .thenReturn(status = OK, body = digitalContactPreferenceJson)
  }
}
