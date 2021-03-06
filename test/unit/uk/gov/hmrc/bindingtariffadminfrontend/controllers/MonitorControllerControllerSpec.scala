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

import akka.stream.Materializer
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import play.api.http.Status.OK
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.{Configuration, Environment}
import uk.gov.hmrc.bindingtariffadminfrontend.config.AppConfig
import uk.gov.hmrc.bindingtariffadminfrontend.service.AdminMonitorService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class MonitorControllerControllerSpec extends WordSpec
  with Matchers
  with UnitSpec
  with MockitoSugar
  with WithFakeApplication
  with BeforeAndAfterEach {

  private val env = Environment.simple()
  private val configuration = Configuration.load(env)
  private val messageApi = new DefaultMessagesApi(env, configuration, new DefaultLangs(configuration))
  private val appConfig = mock[AppConfig]
  private val service = mock[AdminMonitorService]
  private implicit val mat: Materializer = fakeApplication.materializer
  private val controller = new MonitorController(new SuccessfulAuthenticatedAction, service, messageApi, appConfig)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    given(appConfig.analyticsToken) willReturn "token"
    given(appConfig.analyticsHost) willReturn "host"
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    Mockito.reset(appConfig)
  }

  "GET /" should {
    "return 200" in {
      given(service.countCases(any[HeaderCarrier])).willReturn(Future.successful(1))
      given(service.countUnpublishedFiles(any[HeaderCarrier])).willReturn(Future.successful(1))
      given(service.countPublishedFiles(any[HeaderCarrier])).willReturn(Future.successful(1))

      val result: Result = await(controller.get()(FakeRequest()))
      status(result) shouldBe OK
      bodyOf(result) should include("monitor-heading")
    }
  }

}