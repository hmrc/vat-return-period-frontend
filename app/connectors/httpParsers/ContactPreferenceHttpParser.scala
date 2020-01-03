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

package connectors.httpParsers

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.contactPreferences.ContactPreference
import models.contactPreferences.ContactPreference._
import models.errors.{ServerSideError, UnexpectedJsonFormat}
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Failure, Success, Try}

object ContactPreferenceHttpParser {

  implicit object ContactPreferenceReads extends HttpReads[HttpGetResult[ContactPreference]] {

    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[ContactPreference] = {

      response.status match {
        case Status.OK => Try {
          response.json.as[ContactPreference]
        } match {
          case Success(parsedModel) => parsedModel.preference.toUpperCase match {
            case `digital` | `paper` => Right(ContactPreference(parsedModel.preference.toUpperCase()))
            case _ =>
              Logger.warn(s"[ContactPreferencesHttpParser][read]: Invalid preference type received from Contact Preferences")
              Left(UnexpectedJsonFormat)
          }
          case Failure(reason) =>
            Logger.debug(s"[ContactPreferenceHttpParser][ContactPreferenceReads]: Invalid Json - $reason")
            Logger.warn("[ContactPreferenceHttpParser][ContactPreferenceReads]: Invalid Json returned")
            Left(UnexpectedJsonFormat)
        }
        case status =>
          Logger.warn(s"[ContactPreferenceHttpParser][ContactPreferenceReads]: Unexpected Response, Status $status returned")
          Left(ServerSideError(s"$status", "Received downstream error when retrieving contact preferences."))
      }
    }
  }
}
