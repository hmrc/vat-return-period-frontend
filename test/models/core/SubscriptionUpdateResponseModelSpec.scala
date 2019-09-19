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

package models.core

import base.BaseSpec
import play.api.libs.json.{JsObject, Json}

class SubscriptionUpdateResponseModelSpec extends BaseSpec {

  val correctSubscriptionUpdateResponseJson: JsObject = Json.obj("formBundle" -> "123456789")

  val correctSubscriptionUpdateResponseModel: SubscriptionUpdateResponseModel = SubscriptionUpdateResponseModel("123456789")

  "Formats" should {

    "parse correctly from json" in {
      correctSubscriptionUpdateResponseJson.as[SubscriptionUpdateResponseModel] shouldBe correctSubscriptionUpdateResponseModel
    }

    "parse correctly to json" in {
      Json.toJson(correctSubscriptionUpdateResponseModel) shouldBe correctSubscriptionUpdateResponseJson
    }
  }

}
