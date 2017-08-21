/*
 * Copyright 2017 Maalka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package actors.flows

import javax.inject.Inject

import actors.validators.bedes.BedesDensityValidatorProps
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import com.maalka.bedes.BEDESTransformResult
import play.api.Configuration
import play.api.libs.json.Json


class HospitalValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                       override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  override val bedesUse = Seq("Hospital (General Medical & Surgical)", "Health care-Inpatient hospital")

  val parentValidator = "Hospital (General Medical & Surgical)"

  override val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 211, 211),
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 54000, 1426994))

  override val densityFields = Seq(
    BedesValidationThreshold("Health Care-Inpatient Hospital Workers on Main Shift Quantity", 1.2, 4.0),
    BedesValidationThreshold("Health Care-Inpatient Hospital Staffed Beds Quantity", 0.2, 1.1),
    BedesValidationThreshold("Health Care-Inpatient Hospital Medical Equipment Quantity", 0, 0.016)
  )
}

