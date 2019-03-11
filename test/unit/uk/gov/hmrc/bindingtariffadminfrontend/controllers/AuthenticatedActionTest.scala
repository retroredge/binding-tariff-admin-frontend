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

package uk.gov.hmrc.bindingtariffadminfrontend.controllers

import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.{Headers, Request, Result, Results}
import uk.gov.hmrc.bindingtariffadminfrontend.config.AppConfig
import uk.gov.hmrc.bindingtariffadminfrontend.model.{AuthenticatedRequest, Credentials}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class AuthenticatedActionTest extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  private val config = mock[AppConfig]
  private val request = mock[Request[_]]
  private val headers = mock[Headers]
  private val block= mock[AuthenticatedRequest[_] => Future[Result]]
  private val action = new AuthenticatedAction(config)

  override def beforeEach(): Unit = {
    super.beforeEach()
    given(request.headers) willReturn headers
  }

  override def afterEach(): Unit = {
    super.afterEach()
    reset(config, request, block)
  }

  "Authenticated Action" should {
    val username = "username"
    val password = "password"
    val hash = "5E884898DA28047151D0E56F8DC6292773603D0D6AABBDD62A11EF721D1542D8"

    "Permit valid credentials" in {
      givenAnOperatorIsPermittedWith(Credentials(username, hash))
      givenTheRequestHasAuthorization(username, password)
      givenTheBlockExecutesSuccessfully()

      await(action.invokeBlock(request, block)) shouldBe Results.Ok

      verify(block).apply(AuthenticatedRequest(username, request))
    }

    "Not permit missing credentials" in {
      givenAnOperatorIsPermittedWith(Credentials(username, hash))
      givenTheRequestHasNoAuthorization()

      await(action.invokeBlock(request, block)) shouldBe Results.Unauthorized.withHeaders("WWW-Authenticate" -> "Basic realm=Unauthorized")

      verify(block, never()).apply(any[AuthenticatedRequest[_]])
    }

    "Not permit invalid credentials" in {
      givenAnOperatorIsPermittedWith(Credentials(username, hash))
      givenTheRequestHasAuthorization("other", "other")

      await(action.invokeBlock(request, block)) shouldBe Results.Unauthorized.withHeaders("WWW-Authenticate" -> "Basic realm=Unauthorized")

      verify(block, never()).apply(any[AuthenticatedRequest[_]])
    }

    "Handle invalid Basic header" in {
      givenAnOperatorIsPermittedWith(Credentials(username, hash))
      givenTheRequestHasAuthorization("-")

      await(action.invokeBlock(request, block)) shouldBe Results.Unauthorized.withHeaders("WWW-Authenticate" -> "Basic realm=Unauthorized")

      verify(block, never()).apply(any[AuthenticatedRequest[_]])
    }

    "Propagate any block errors" in {
      givenAnOperatorIsPermittedWith(Credentials(username, hash))
      givenTheRequestHasAuthorization(username, password)
      givenTheBlockFails()

      intercept[RuntimeException] {
        await(action.invokeBlock(request, block))
      }.getMessage shouldBe "Error"
    }
  }

  private def givenTheBlockExecutesSuccessfully(): Unit = {
    given(block.apply(any[AuthenticatedRequest[_]])) willReturn Future.successful(Results.Ok)
  }

  private def givenTheBlockFails(): Unit = {
    given(block.apply(any[AuthenticatedRequest[_]])) willReturn Future.failed(new RuntimeException("Error"))
  }

  private def givenTheRequestHasAuthorization(username: String, password: String): Unit = {
    given(headers.get("Authorization")) willReturn Some("Basic " + BaseEncoding.base64().encode(s"$username:$password".getBytes(Charsets.UTF_8)))
  }

  private def givenTheRequestHasAuthorization(content: String): Unit = {
    given(headers.get("Authorization")) willReturn Some(content)
  }

  private def givenTheRequestHasNoAuthorization(): Unit = {
    given(headers.get("Authorization")) willReturn None
  }

  private def givenAnOperatorIsPermittedWith(credentials: Credentials): Unit = {
    given(config.credentials) willReturn Seq(credentials)
  }

}
