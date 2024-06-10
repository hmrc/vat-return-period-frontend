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

package audit.models

import base.BaseSpec
import models.returnFrequency.Jan
import play.api.libs.json.Json

class StartJourneyAuditModelSpec extends BaseSpec {

  val userAuditModel: StartJourneyAuditModel = StartJourneyAuditModel(user, Jan)

  "The StartJourneyAuditModel" should {

    "have the correct transaction name" in {
      userAuditModel.transactionName shouldBe "change-vat-return-frequency"
    }

    "have the correct audit type" in {
      userAuditModel.auditType shouldBe "ChangeVatSubscriptionDetailsStart"
    }

    "have the correct detail" when {

      "the user is an agent" in {
        val agentAuditModel = userAuditModel.copy(user = agentUser)

        agentAuditModel.detail shouldBe Json.obj(
          "isAgent" -> true,
          "agentReferenceNumber" -> arn,
          "vrn" -> vrn,
          "currentReturnFrequency" -> "January, April, July and October"
        )
      }

      "the user is not an agent" in {
        userAuditModel.detail shouldBe Json.obj(
          "isAgent" -> false,
          "vrn" -> vrn,
          "currentReturnFrequency" -> "January, April, July and October"
        )
      }
    }
  }
}
