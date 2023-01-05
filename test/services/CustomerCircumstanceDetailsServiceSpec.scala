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

package services

import assets.BaseTestConstants._
import assets.CircumstanceDetailsTestConstants._
import base.BaseSpec
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import mocks.connectors.MockSubscriptionConnector
import models.circumstanceInfo.CircumstanceDetails
import models.errors.ServerSideError
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.Future

class CustomerCircumstanceDetailsServiceSpec extends BaseSpec with MockSubscriptionConnector {

  object TestCustomerCircumstanceDetailsService extends CustomerCircumstanceDetailsService(mockSubscriptionConnector)

  "CustomerCircumstanceDetailsService" should {

    def result: Future[HttpGetResult[CircumstanceDetails]] = TestCustomerCircumstanceDetailsService.getCustomerCircumstanceDetails(vrn)

    "for getCustomerDetails method" when {

      "called for a Right with CustomerDetails" should {

        "return a CustomerDetailsModel" in {
          setupMockUserDetails(vrn)(Right(circumstanceDetailsModelMax))
          await(result) shouldBe Right(circumstanceDetailsModelMax)
        }
      }

      "given an error should" should {

        "return an Left with an ErrorModel" in {
          setupMockUserDetails(vrn)(Left(ServerSideError("503", "response body")))
          await(result) shouldBe Left(errorModel)
        }
      }
    }
  }
}
