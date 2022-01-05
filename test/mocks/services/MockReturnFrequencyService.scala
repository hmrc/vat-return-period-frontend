/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.httpParsers.ResponseHttpParsers.HttpPutResult
import models.auth.User
import models.errors.ServerSideError
import models.returnFrequency.{ReturnPeriod, SubscriptionUpdateResponseModel}
import org.scalamock.scalatest.MockFactory
import services.ReturnFrequencyService
import uk.gov.hmrc.http.HeaderCarrier
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.{ExecutionContext, Future}

trait MockReturnFrequencyService extends AnyWordSpecLike with MockFactory {

  val mockReturnFrequencyService: ReturnFrequencyService = mock[ReturnFrequencyService]

  def setupMockReturnFrequencyService[A](response: HttpPutResult[SubscriptionUpdateResponseModel]): Unit  = {
    (mockReturnFrequencyService.updateReturnFrequency(_: String, _: ReturnPeriod)(_: HeaderCarrier, _: ExecutionContext, _: User[A]))
      .expects(*, *, *, *, *)
      .returns(Future.successful(response))
  }

  def setupMockReturnFrequencyServiceWithSuccess(): Unit =
    setupMockReturnFrequencyService(Right(SubscriptionUpdateResponseModel("12345")))
  def setupMockReturnFrequencyServiceWithConflict(): Unit =
    setupMockReturnFrequencyService(Left(ServerSideError("409", "Error")))
  def setupMockReturnFrequencyServiceWithFailure(): Unit =
    setupMockReturnFrequencyService(Left(ServerSideError("", "")))
}
