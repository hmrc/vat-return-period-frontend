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

package testOnly.connector

import javax.inject.Inject
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import testOnly.TestOnlyAppConfig
import testOnly.models.VatReturnPeriodFeatureSwitchModel

import scala.concurrent.{ExecutionContext, Future}

class VatReturnPeriodFeaturesConnector @Inject()(val http: HttpClient,
                                                 val appConfig: TestOnlyAppConfig) {

  def getFeatures(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[VatReturnPeriodFeatureSwitchModel] = {
    lazy val url = s"${appConfig.vatReturnPeriodUrl}/test-only/feature-switch"
    http.GET[VatReturnPeriodFeatureSwitchModel](url)
  }

  def postFeatures(vatReturnPeriodFeatures: VatReturnPeriodFeatureSwitchModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    lazy val url = s"${appConfig.vatReturnPeriodUrl}/test-only/feature-switch"
    http.POST[VatReturnPeriodFeatureSwitchModel, HttpResponse](url, vatReturnPeriodFeatures)
  }

}
