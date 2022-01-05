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

import assets.BaseTestConstants._
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.circumstanceInfo.CircumstanceDetails
import models.errors.ServerSideError
import org.scalamock.scalatest.MockFactory
import services.CustomerCircumstanceDetailsService
import uk.gov.hmrc.http.HeaderCarrier
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.{ExecutionContext, Future}

trait MockCustomerCircumstanceDetailsService extends AnyWordSpecLike with MockFactory {

  val mockCustomerDetailsService: CustomerCircumstanceDetailsService = mock[CustomerCircumstanceDetailsService]

  def setupMockCustomerDetails(vrn: String)(response: HttpGetResult[CircumstanceDetails]): Unit = {
    (mockCustomerDetailsService.getCustomerCircumstanceDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(response))
  }

  def mockCustomerDetailsSuccess(customerDetails: CircumstanceDetails): Unit = setupMockCustomerDetails(vrn)(Right(customerDetails))
  def mockCustomerDetailsError(): Unit = setupMockCustomerDetails(vrn)(Left(ServerSideError("", "")))
}

