/*
 * Copyright 2021 HM Revenue & Customs
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

import common.SessionKeys
import connectors.VatSubscriptionConnector
import connectors.httpParsers.ResponseHttpParsers.HttpPutResult
import javax.inject.{Inject, Singleton}
import models.auth.User
import models.returnFrequency.{ReturnPeriod, SubscriptionUpdateResponseModel, UpdateReturnPeriod}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReturnFrequencyService @Inject()(subscriptionConnector: VatSubscriptionConnector) {

  def updateReturnFrequency(vrn: String, frequency: ReturnPeriod)
                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, user: User[_])
  : Future[HttpPutResult[SubscriptionUpdateResponseModel]] = {

    val updateReturnPeriod = UpdateReturnPeriod(frequency.internalId, user.session.get(SessionKeys.verifiedAgentEmail))
    subscriptionConnector.updateReturnFrequency(vrn, updateReturnPeriod)
  }
}
