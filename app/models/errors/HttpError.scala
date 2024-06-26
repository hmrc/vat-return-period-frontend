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

package models.errors

sealed trait HttpError {
  def message: String
}

object UnexpectedJsonFormat extends HttpError {
  override val message: String = "The server you are connecting to returned unexpected JSON."
}

case class ServerSideError(code: String, errorResponse: String) extends HttpError {
  override val message: String = s"The server you are connecting to returned an error. " +
    s"[ServerSideError]- RESPONSE status: $code, body: $errorResponse"
}
