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


class SupermarketValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                          override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  override val bedesUse = Seq("Supermarket/Grocery Store", "Food sales-Grocery store")

  val parentValidator = "Supermarket/Grocery Store"

  override val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 328, 828),
    //BedesValidationThreshold("Food Sales-Grocery Store Gross Area", 19785, 92961),
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 19785, 92961),

      BedesValidationThreshold("Food Sales-Grocery Store Business Average Weekly Hours", 98, 164)

  )

  override val densityFields = Seq(
    BedesValidationThreshold("Food Sales-Grocery Store Workers on Main Shift Quantity", 0.4, 1.5),
    BedesValidationThreshold("Food Sales-Grocery Store Refrigeration Walk-in Quantity", 0.4, 1.5)
  )
}

