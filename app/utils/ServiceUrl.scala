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

package utils

import config.AppConfig
import models.auth.User
import play.api.mvc.Request

class ServiceUrl {

  def generateUrl(implicit request: Request[_], appConfig: AppConfig): Option[String] = request match {
    case user: User[_] => if (user.isAgent) { Some(appConfig.agentClientLookupUrl) }
                          else { Some(appConfig.vatDetailsUrl) }
    case _ => None
  }

}
