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

package uk.gov.hmrc.bindingtariffadminfrontend.model.classification

import java.time.Instant

import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.bindingtariffadminfrontend.model.Anonymize._
import uk.gov.hmrc.bindingtariffadminfrontend.model.classification.ApplicationType.ApplicationType
import uk.gov.hmrc.bindingtariffadminfrontend.model.classification.LiabilityStatus.LiabilityStatus
import uk.gov.hmrc.bindingtariffadminfrontend.util.JsonUtil
import uk.gov.hmrc.play.json.Union


sealed trait Application {
  val `type`: ApplicationType

  def anonymize: Application
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
  override val `type`: ApplicationType.Value = ApplicationType.BTI

  override def anonymize: Application = this.copy(
    holder = holder.anonymize,
    contact = contact.anonymize,
    agent = agent.map(_.anonymize),
    confidentialInformation = confidentialInformation.map(anonymizing)
  )

}

object BTIApplication {
  implicit val outboundFormat: OFormat[BTIApplication] = Json.format[BTIApplication]
}

case class LiabilityOrder
(
  contact: Contact,
  goodName: Option[String],
  status: LiabilityStatus,
  traderName: String,
  entryDate: Option[Instant] = None,
  entryNumber: Option[String] = None
) extends Application {
  override val `type`: ApplicationType.Value = ApplicationType.LIABILITY_ORDER

  override def anonymize: Application = this.copy(
    contact = contact.anonymize
  )
}

object LiabilityOrder {
  implicit val outboundFormat: OFormat[LiabilityOrder] = Json.format[LiabilityOrder]
}

object LiabilityStatus extends Enumeration {
  type LiabilityStatus = Value
  val LIVE, NON_LIVE = Value
  implicit val format: Format[LiabilityStatus.Value] = JsonUtil.format(LiabilityStatus)
}

object ApplicationType extends Enumeration {
  type ApplicationType = Value
  val BTI, LIABILITY_ORDER = Value
  implicit val format: Format[LiabilityStatus.Value] = JsonUtil.format(LiabilityStatus)
}
