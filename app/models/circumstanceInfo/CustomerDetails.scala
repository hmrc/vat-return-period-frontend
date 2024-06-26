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

package models.circumstanceInfo

import play.api.libs.functional.syntax._
import play.api.libs.json._
import utils.{JsonObjectSugar, JsonReadUtil}

case class CustomerDetails(firstName: Option[String],
                           lastName: Option[String],
                           organisationName: Option[String],
                           tradingName: Option[String],
                           isInsolvent: Boolean,
                           continueToTrade: Option[Boolean],
                           insolvencyType: Option[String]) {

  val isOrganisation: Boolean = organisationName.isDefined
  val userName: Option[String] = {
    val name = s"${firstName.getOrElse("")} ${lastName.getOrElse("")}".trim
    if (name.isEmpty) None else Some(name)
  }
  val businessName: Option[String] = if (isOrganisation) organisationName else userName
  val clientName: Option[String] = if (tradingName.isDefined) tradingName else businessName

  val exemptInsolvencyTypes = Seq("07", "12", "13", "14")
  val blockedInsolvencyTypes = Seq("08", "09", "10", "15")

  val isInsolventWithoutAccess: Boolean = (isInsolvent, insolvencyType) match {
    case (true, Some(inType)) if exemptInsolvencyTypes.contains(inType) => false
    case (true, Some(inType)) if blockedInsolvencyTypes.contains(inType) => true
    case (true, _) if continueToTrade.contains(false) => true
    case _ => false
  }
}

object CustomerDetails extends JsonReadUtil with JsonObjectSugar {

  private val firstNamePath = __ \ "firstName"
  private val lastNamePath = __ \ "lastName"
  private val organisationNamePath = __ \ "organisationName"
  private val tradingNamePath = __ \ "tradingName"
  private val isInsolventPath = __ \ "isInsolvent"
  private val continueToTradePath = __ \ "continueToTrade"
  private val insolvencyTypePath = __ \ "insolvencyType"

  implicit val reads: Reads[CustomerDetails] = (
    firstNamePath.readOpt[String] and
    lastNamePath.readOpt[String] and
    organisationNamePath.readOpt[String] and
    tradingNamePath.readOpt[String] and
    isInsolventPath.read[Boolean] and
    continueToTradePath.readOpt[Boolean] and
    insolvencyTypePath.readOpt[String]
  ) (CustomerDetails.apply _)

  implicit val writes: Writes[CustomerDetails] = Writes {
    model =>
      jsonObjNoNulls(
        "firstName" -> model.firstName,
        "lastName" -> model.lastName,
        "organisationName" -> model.organisationName,
        "tradingName" -> model.tradingName,
        "isInsolvent" -> model.isInsolvent,
        "continueToTrade" -> model.continueToTrade,
        "insolvencyType" -> model.insolvencyType
      )
  }
}
