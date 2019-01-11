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

import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.bindingtariffadminfrontend.config.AppConfig
import uk.gov.hmrc.bindingtariffadminfrontend.model.MigrationStatus
import uk.gov.hmrc.bindingtariffadminfrontend.service.DataMigrationService
import uk.gov.hmrc.bindingtariffadminfrontend.views.html.data_migration_state
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DataMigrationStateController @Inject()(service: DataMigrationService,
                                             val messagesApi: MessagesApi,
                                             implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  def get: Action[AnyContent] = Action.async { implicit request =>
    for {
      counts <- service.counts
      state <- service.getState
      view = data_migration_state(state, counts)
    } yield Ok(view)
  }

  def delete(status: Option[String]): Action[AnyContent] = Action.async { implicit request =>
    val statusFilter = status.flatMap(MigrationStatus(_))
    service.clear(statusFilter)
      .map(_ => Redirect(routes.DataMigrationStateController.get()))
  }
}