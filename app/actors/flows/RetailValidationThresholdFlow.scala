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

import actors.validators.bedes.BedesDensityValidatorProps
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import javax.inject._

import com.maalka.bedes.BEDESTransformResult
import play.api.Configuration
import play.api.libs.json.Json

class RetailValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  override val bedesUse = Seq("Retail Store", "Retail-Dry goods retail")

  val parentValidator = "Retail Store"

  override val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 69, 400),
    //BedesValidationThreshold("Retail-Dry Goods Retail Gross Area", 7016, 127455),
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 7016, 127455),

      BedesValidationThreshold("Retail-Dry Goods Retail Business Average Weekly Hours", 71, 168)
  )

  override val densityFields = Seq(
    BedesValidationThreshold("Retail-Dry Goods Retail Workers on Main Shift Quantity", 0.1, 1),
    BedesValidationThreshold("Retail-Dry Goods Retail Cash Register Quantity", 0.1, 0.8),
    BedesValidationThreshold("Retail-Dry Goods Retail Computer Quantity", 0.0, 0.8),
    BedesValidationThreshold("Retail-Dry Goods Retail Refrigeration Walk-in Quantity", 0, 0.1),
    BedesValidationThreshold("Retail-Dry Goods Retail Commercial Refrigeration Case Quantity", 0, 1)
  )
}

