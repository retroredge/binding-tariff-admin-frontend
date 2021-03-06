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

package uk.gov.hmrc.bindingtariffadminfrontend.model

import java.time.Instant

import play.api.libs.json.Format
import play.json.extra.Jsonx
import uk.gov.hmrc.bindingtariffadminfrontend.model.classification.{Attachment, Operator}
import uk.gov.hmrc.bindingtariffadminfrontend.util.FilenameUtil

case class MigratedAttachment
(
  public: Boolean = false,
  name: String,
  operator: Option[Operator] = None,
  timestamp: Instant
) {
  def id: String = FilenameUtil.toID(name)

  def asAttachment: Attachment = {
    Attachment(id = id, public = public, operator = operator, timestamp = timestamp)
  }
}

object MigratedAttachment {
  implicit val format: Format[MigratedAttachment] = Jsonx.formatCaseClass[MigratedAttachment]
}