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

package audit

import audit.models.TestExtendedAuditModel
import base.BaseSpec
import config.FrontendAppConfig
import play.api.http.HeaderNames
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent

import scala.concurrent.{ExecutionContext, Future}

class AuditingServiceSpec extends BaseSpec {

  val mockConfiguration: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  val mockAuditConnector: FrontendAuditConnector = mock[FrontendAuditConnector]

  def setupSendExtendedEvent()(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit = {
    (mockAuditConnector.sendExtendedEvent(_: ExtendedDataEvent)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(Success))
  }

  val mockAuditService = new AuditService(mockConfiguration, mockAuditConnector)

  "AuditService" should {

    "when calling the referer method" should {

      "extract the referer if there is one" in {
        val testPath = "/test/path"
        mockAuditService.referrer(HeaderCarrier().withExtraHeaders(HeaderNames.REFERER -> testPath)) shouldBe testPath
      }

      "default to hyphen '-' if there is no referrer" in {
        mockAuditService.referrer(HeaderCarrier()) shouldBe "-"
      }
    }

    "given an ExtendedAuditModel" should {

      "extract the data and pass it into the AuditConnector" in {

        val testModel = new TestExtendedAuditModel("foo", "bar")
        val testPath = "/test/path"

        setupSendExtendedEvent()

        mockAuditService.extendedAudit(testModel, Some(testPath))
      }
    }
  }
}
