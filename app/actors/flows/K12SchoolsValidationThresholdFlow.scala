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

import actors.validators.bedes.{BedesDensityValidator, BedesDensityValidatorProps}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import com.maalka.bedes.BEDESTransformResult
import play.api.Configuration
import play.api.libs.json.Json

class K12SchoolsValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                         override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  val bedesUse = Seq("K-12 School", "Education")

  val parentValidator = "K-12 School"

  val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 56, 208),
    //BedesValidationThreshold("Education Gross Area", 23211, 284599))
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 23211, 284599)
  )


  val densityFields = Seq(
    BedesValidationThreshold("Education Computer Quantity", 0.7, 5.2),
    BedesValidationThreshold("Education Refrigeration Walk-in Quantity", 0.0, 0.04)
  )
}


