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

import java.time.ZonedDateTime

import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.bindingtariffadminfrontend.model
import uk.gov.hmrc.bindingtariffadminfrontend.model.ApplicationType.ApplicationType
import uk.gov.hmrc.bindingtariffadminfrontend.model.LiabilityStatus.LiabilityStatus
import uk.gov.hmrc.bindingtariffadminfrontend.util.EnumJson
import uk.gov.hmrc.play.json.Union


sealed trait Application {
  val `type`: ApplicationType
}

object Application {
  implicit val format: Format[Application] = Union.from[Application]("type")
    .and[BTIApplication](ApplicationType.BTI.toString)
    .and[LiabilityOrder](ApplicationType.LIABILITY_ORDER.toString)
    .format
}

case class BTIApplication
(
  holder: EORIDetails,
  contact: Contact,
  agent: Option[AgentDetails] = None,
  offline: Boolean = false,
  goodName: String,
  goodDescription: String,
  confidentialInformation: Option[String] = None,
  otherInformation: Option[String] = None,
  reissuedBTIReference: Option[String] = None,
  relatedBTIReference: Option[String] = None,
  knownLegalProceedings: Option[String] = None,
  envisagedCommodityCode: Option[String] = None,
  sampleToBeProvided: Boolean = false,
  sampleToBeReturned: Boolean = false
) extends Application {
  override val `type`: model.ApplicationType.Value = ApplicationType.BTI
}

object BTIApplication {
  implicit val outboundFormat: OFormat[BTIApplication] = Json.format[BTIApplication]
}

case class LiabilityOrder
(
  holder: EORIDetails,
  contact: Contact,
  status: LiabilityStatus,
  port: String,
  entryNumber: String,
  endDate: ZonedDateTime
) extends Application {
  override val `type`: model.ApplicationType.Value = ApplicationType.LIABILITY_ORDER
}

object LiabilityOrder {
  implicit val outboundFormat: OFormat[LiabilityOrder] = Json.format[LiabilityOrder]
}

object LiabilityStatus extends Enumeration {
  type LiabilityStatus = Value
  val LIVE, NON_LIVE = Value
  implicit val format: Format[model.LiabilityStatus.Value] = EnumJson.format(LiabilityStatus)
}

object ApplicationType extends Enumeration {
  type ApplicationType = Value
  val BTI, LIABILITY_ORDER = Value
  implicit val format: Format[model.LiabilityStatus.Value] = EnumJson.format(LiabilityStatus)
}