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

package models

import common.AuthKeys
import play.api.mvc.{Request, WrappedRequest}
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments, InternalError}

case class AgentUser[A](arn: String) (implicit request: Request[A]) extends WrappedRequest[A](request)

object AgentUser {
  def apply[A](enrolments: Enrolments)(implicit request: Request[A]): AgentUser[A] =
    enrolments.enrolments.collectFirst {
      case Enrolment(AuthKeys.agentEnrolmentId, EnrolmentIdentifier(_, arn) :: _, _, _) => AgentUser(arn)
    }.getOrElse(throw InternalError("Agent Service Enrolment Missing"))
}
