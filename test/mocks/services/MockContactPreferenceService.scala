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

package mocks.services

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.contactPreferences.ContactPreference
import models.errors.ServerSideError
import org.scalamock.scalatest.MockFactory
import services.ContactPreferenceService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

trait MockContactPreferenceService extends UnitSpec with MockFactory {

  val mockContactPreferenceService: ContactPreferenceService = mock[ContactPreferenceService]

  def setupMockContactPreference(vrn: String)(response: HttpGetResult[ContactPreference]): Unit = {
    (mockContactPreferenceService.getContactPreference(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(response))
  }

  def mockContactPreferenceSuccess(contactPreference: ContactPreference): Unit = setupMockContactPreference("999999999")(Right(contactPreference))

  def mockContactPreferenceError(): Unit = setupMockContactPreference("999999999")(Left(ServerSideError("", "")))

}
