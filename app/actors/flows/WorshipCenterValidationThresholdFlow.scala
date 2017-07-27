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

class WorshipCenterValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                            override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  val bedesUse = Seq("Worship Facility", "Assembly-Religious")

  val parentValidator = "Worship Facility"

  val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 29, 228),
    //BedesValidationThreshold("Assembly-Religious Gross Area", 5136, 110000),
    BedesValidationThreshold("EPA Calculated Gross Floor AreaEPA Calculated Gross Floor Area", 5136, 110000),

    BedesValidationThreshold("Assembly-Religious Business Average Weekly Hours", 10, 98)
  )

  val densityFields = Seq(
    BedesValidationThreshold("Assembly-Religious Computer Quantity", 0, 0.9),
    BedesValidationThreshold("Assembly-Religious Capacity Quantity", 6, 51),
    BedesValidationThreshold("Assembly-Religious Commercial Refrigeration Quantity", 0, 0.2)
    // enter fields here
  )
}
