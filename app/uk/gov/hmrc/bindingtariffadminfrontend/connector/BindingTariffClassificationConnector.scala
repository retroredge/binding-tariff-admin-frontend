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

package uk.gov.hmrc.bindingtariffadminfrontend.connector

import com.google.inject.Inject
import javax.inject.Singleton
import uk.gov.hmrc.bindingtariffadminfrontend.config.AppConfig
import uk.gov.hmrc.bindingtariffadminfrontend.model.classification.{Case, CaseSearch, Event, EventSearch}
import uk.gov.hmrc.bindingtariffadminfrontend.model.{Paged, Pagination}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BindingTariffClassificationConnector @Inject()(configuration: AppConfig, client: AuthenticatedHttpClient) {

  def upsertCase(c: Case)(implicit hc: HeaderCarrier): Future[Case] = {
    val url = s"${configuration.classificationBackendUrl}/cases/${c.reference}"
    client.PUT[Case, Case](url = url, body = c)
  }

  def createEvent(ref: String, event: Event)(implicit hc: HeaderCarrier): Future[Event] = {
    val url = s"${configuration.classificationBackendUrl}/cases/$ref/events"
    client.POST[Event, Event](url = url, body = event)
  }

  def getEvents(ref: String, pagination: Pagination)(implicit hc: HeaderCarrier): Future[Paged[Event]] = {
    val url = s"${configuration.classificationBackendUrl}/cases/$ref/events"
    client.GET[Paged[Event]](url = url)
  }

  def getEvents(search: EventSearch, pagination: Pagination)(implicit hc: HeaderCarrier): Future[Paged[Event]] = {
    val queryParam = Seq(Pagination.bindable.unbind("", pagination), EventSearch.bindable.unbind("", search)).filter(_.nonEmpty).mkString("&")
    val url = s"${configuration.classificationBackendUrl}/events?$queryParam"
    client.GET[Paged[Event]](url = url)
  }

  def getCase(ref: String)(implicit hc: HeaderCarrier): Future[Option[Case]] = {
    val url = s"${configuration.classificationBackendUrl}/cases/$ref"
    client.GET[Option[Case]](url = url)
  }

  def getCases(search: CaseSearch, pagination: Pagination)(implicit hc: HeaderCarrier): Future[Paged[Case]] = {
    val queryParam = Seq(Pagination.bindable.unbind("", pagination), CaseSearch.bindable.unbind("", search)).filter(_.nonEmpty).mkString("&")
    val url = s"${configuration.classificationBackendUrl}/cases?$queryParam"
    client.GET[Paged[Case]](url = url)
  }

  def deleteCases()(implicit hc: HeaderCarrier): Future[Unit] = {
    val url = s"${configuration.classificationBackendUrl}/cases"
    client.DELETE(url = url).map(_ => ())
  }

  def deleteEvents()(implicit hc: HeaderCarrier): Future[Unit] = {
    val url = s"${configuration.classificationBackendUrl}/events"
    client.DELETE(url = url).map(_ => ())
  }

  def runDaysElapsed(implicit hc: HeaderCarrier): Future[Unit] = {
    val url = s"${configuration.classificationBackendUrl}/scheduler/days-elapsed"
    client.PUT(url = url, body = "").map(_ => ())
  }

}
