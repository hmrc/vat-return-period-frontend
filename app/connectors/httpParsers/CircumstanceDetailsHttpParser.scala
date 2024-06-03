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

package connectors.httpParsers

import connectors.httpParsers.ResponseHttpParsers.HttpResult
import models.circumstanceInfo.CircumstanceDetails
import models.errors.{ServerSideError, UnexpectedJsonFormat}
import utils.LoggerUtil
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Failure, Success, Try}

object CircumstanceDetailsHttpParser extends LoggerUtil {

  implicit object CircumstanceDetailsReads extends HttpReads[HttpResult[CircumstanceDetails]] {

    override def read(method: String, url: String, response: HttpResponse): HttpResult[CircumstanceDetails] = {

      response.status match {
        case Status.OK => Try {
          response.json.as[CircumstanceDetails]
        } match {
          case Success(parsedModel) => Right(parsedModel)
          case Failure(reason) =>
            logger.debug(s"[CustomerDetailsHttpParser][CustomerDetailsReads]: Invalid Json - $reason")
            logger.warn("[CustomerDetailsHttpParser][CustomerDetailsReads]: Invalid Json returned")
            Left(UnexpectedJsonFormat)
        } case status =>
          logger.warn(s"[CustomerCircumstancesHttpParser][read]: Unexpected Response, Status $status returned")
          Left(ServerSideError(s"$status", "Received downstream error when retrieving customer details."))
      }
    }
  }
}
