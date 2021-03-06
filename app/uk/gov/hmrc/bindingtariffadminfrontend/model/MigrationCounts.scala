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

import uk.gov.hmrc.bindingtariffadminfrontend.model.MigrationStatus.MigrationStatus

class MigrationCounts(counts: Map[MigrationStatus, Int]) {

  def get(status: MigrationStatus): Int = {
    counts.getOrElse(status, 0)
  }

  def total: Int = {
    counts.foldLeft(0)(_ + _._2)
  }

  def hasUnprocessed: Boolean = {
    get(MigrationStatus.UNPROCESSED) > 0
  }

  def processed: Int = {
    counts.filter(_._1 != MigrationStatus.UNPROCESSED).foldLeft(0)(_ + _._2)
  }

  def isEmpty: Boolean = {
    counts.isEmpty
  }
}
