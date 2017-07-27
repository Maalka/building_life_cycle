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

class MedicalOfficeValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                            override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  val bedesUse = Seq("Medical Office", "Health care-Outpatient non-diagnostic")

  val parentValidator = "Medical Office"

  val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 84, 570),
    //BedesValidationThreshold("Health Care-Outpatient Non-diagnostic Gross Area", 7114, 202354))
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 7114, 202354)
  )

  val densityFields = Seq(
    BedesValidationThreshold("Health Care-Outpatient Non-diagnostic Business Average Weekly Hours", 40, 112),
    BedesValidationThreshold("Health Care-Outpatient Non-diagnostic Workers on Main Shift Quantity", 0.8, 5)
  )
}

