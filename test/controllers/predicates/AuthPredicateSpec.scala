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

package controllers.predicates

import assets.CircumstanceDetailsTestConstants.{circumstanceDetailsInsolvent, circumstanceDetailsModelMax}
import assets.messages.AuthMessages
import common.SessionKeys.insolventWithoutAccessKey
import mocks.MockAuth
import models.errors.ServerSideError
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{BearerTokenExpired, InsufficientEnrolments, UnsupportedCredentialRole}

import scala.concurrent.Future

class AuthPredicateSpec extends MockAuth {

  def target(): Action[AnyContent] = mockAuthPredicate.async {
    Future.successful(Ok("hello"))
  }

  "Calling .invokeBlock" when {

    "user is Agent" when {

      "the session contains mtdVatvcClientVrn" when {

        "agent has delegated authority for the VRN" when {

          "agent has HMRC-AS-AGENT enrolment" should {

            lazy val result = target()(FakeRequest().withSession("mtdVatvcClientVrn" -> "999999999"))

            "allow the request through" in {
              mockAuthoriseAsAgent(agentAuthorisedResponse, Future.successful(agentServicesEnrolment))

              status(result) shouldBe Status.OK
            }
          }

          "agent does not have HMRC-AS-AGENT enrolment" should {

            lazy val result = target()(FakeRequest().withSession("mtdVatvcClientVrn" -> "999999999"))

            "return 403" in {
              mockAuthoriseAsAgent(agentAuthorisedResponse, Future.successful(unauthorisedEnrolment))

              status(result) shouldBe Status.FORBIDDEN
            }

            "render Agent unauthorised view" in {
              Jsoup.parse(contentAsString(result)).title() shouldBe AuthMessages.unauthorisedTitle + AuthMessages.mtdfvTitleSuffix
            }
          }
        }

        "agent does not have delegated authority for the VRN" when {

          val authResponse = Future.successful(new ~(Some(Agent), agentServicesEnrolmentWithoutDelegatedAuth))
          lazy val result = target()(FakeRequest().withSession("mtdVatvcClientVrn" -> "999999999"))

          val redirectUrl = mockAppConfig.agentClientUnauthorisedUrl("/")

          "return 303" in {
            mockAuthoriseAsAgent(authResponse, Future.failed(InsufficientEnrolments()))

            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to $redirectUrl" in {
            redirectLocation(result) shouldBe Some(redirectUrl)
          }
        }

        "auth returns a NoActiveSession exception" should {

          val authResponse = Future.successful(new ~(Some(Agent), agentServicesEnrolmentWithoutDelegatedAuth))
          lazy val result = target()(FakeRequest().withSession("mtdVatvcClientVrn" -> "999999999"))

          "return 303" in {
            mockAuthoriseAsAgent(authResponse, Future.failed(BearerTokenExpired()))

            status(result) shouldBe Status.SEE_OTHER
          }

          s"redirect to ${mockAppConfig.signInUrl}" in {
            redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
          }
        }
      }

      "the session does not contain CLIENT_VRN" should {

        val authResponse = Future.successful(new ~(Some(Agent), agentServicesEnrolment))
        lazy val result = target()(FakeRequest())

        val redirectUrl = mockAppConfig.agentClientLookupStartUrl(controllers.returnFrequency.routes.ChooseDatesController.show.url)

        "return 303" in {
          mockAuthorise(authResponse)

          status(result) shouldBe Status.SEE_OTHER
        }

        s"redirect to $redirectUrl" in {
          redirectLocation(result) shouldBe Some(redirectUrl)
        }
      }
    }

    "user is non-Agent" when {

      "user has HMRC-MTD-VAT enrolment" should {

        val authResponse = Future.successful(new ~(Some(Individual), mtdVatEnrolment))
        lazy val result = target()(FakeRequest().withSession(insolventWithoutAccessKey -> "false"))

        "they have a value in session for their insolvency status" when {

          "the value is 'true' (insolvent user not continuing to trade)" should {

            "return Forbidden (403)" in {
              mockAuthorise(authResponse)
              status(target()(insolventRequest)) shouldBe Status.FORBIDDEN
            }
          }

          "the value is 'false' (user permitted to trade)" should {

            "return OK (200)" in {
              mockAuthorise(authResponse)
              status(result) shouldBe Status.OK
            }
          }
        }

        "they do not have a value in session for their insolvency status" when {

          "they are insolvent and not continuing to trade" should {

            lazy val result = {
              setupMockCustomerDetails("999999999")(Right(circumstanceDetailsInsolvent))
              target()(FakeRequest())
            }

            "return Forbidden (403)" in {
              mockAuthorise(authResponse)
              status(result) shouldBe Status.FORBIDDEN
            }

            "add the insolvent flag to the session" in {
              session(result).get(insolventWithoutAccessKey) shouldBe Some("true")
            }
          }

          "they are permitted to trade" should {

            lazy val result = {
              setupMockCustomerDetails("999999999")(Right(circumstanceDetailsModelMax))
              target()(FakeRequest())
            }

            "return OK (200)" in {
              mockAuthorise(authResponse)
              status(result) shouldBe Status.OK
            }

            "add the insolvent flag to the session" in {
              session(result).get(insolventWithoutAccessKey) shouldBe Some("false")
            }
          }

          "there is an error returned from the customer information API" should {

            lazy val result = {
              setupMockCustomerDetails("999999999")(Left(ServerSideError(Status.INTERNAL_SERVER_ERROR.toString, "")))
              target()(FakeRequest())
            }

            "return Internal Server Error (500)" in {
              mockAuthorise(authResponse)
              status(result) shouldBe Status.INTERNAL_SERVER_ERROR
            }
          }
        }
      }

      "user does not have HMRC-MTD-VAT enrolment" should {

        val authResponse = Future.successful(new ~(Some(Individual), otherEnrolment))
        lazy val result = target()(FakeRequest())

        "return 403" in {
          mockAuthorise(authResponse)

          status(result) shouldBe Status.FORBIDDEN
        }

        "render the unauthorised view" in {
          Jsoup.parse(contentAsString(result)).title() shouldBe AuthMessages.unauthorisedTitle + AuthMessages.mtdfvTitleSuffix
        }
      }
    }

    "affinity group is not returned" should {

      val authResponse = Future.successful(new ~(None, otherEnrolment))
      lazy val result = target()(FakeRequest())

      "return 500" in {
        mockAuthorise(authResponse)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "render ISE page" in {
        Jsoup.parse(contentAsString(result)).title() shouldBe AuthMessages.problemWithServiceTitle + AuthMessages.mtdfvTitleSuffix
      }
    }

    "auth returns a NoActiveSession exception" should {

      val authResponse = Future.failed(BearerTokenExpired())
      lazy val result = target()(FakeRequest())

      "return 303" in {
        mockAuthorise(authResponse)

        status(result) shouldBe Status.SEE_OTHER
      }

      s"redirect to ${mockAppConfig.signInUrl}" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "auth returns AuthorisationException" should {

      val authResponse = Future.failed(UnsupportedCredentialRole())
      lazy val result = target()(FakeRequest())

      "return 500" in {
        mockAuthorise(authResponse)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "render ISE page" in {
        Jsoup.parse(contentAsString(result)).title() shouldBe AuthMessages.problemWithServiceTitle + AuthMessages.mtdfvTitleSuffix
      }
    }
  }
}
