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


class HotelValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                    override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  val bedesUse = Seq("Hotel", "Lodging with extended amenities")

  val parentValidator = "Hotel"

  val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 89, 344),
    //BedesValidationThreshold("Lodging with Extended Amenities Gross Area", 21289, 617226))
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 21289, 617226)
  )


  val densityFields = Seq(
    BedesValidationThreshold("Lodging with Extended Amenities Workers on Main Shift Quantity", 0.1, 0.7),
    BedesValidationThreshold("Lodging with Extended Amenities Commercial Refrigeration Quantity", 0, 0.15),
    BedesValidationThreshold("Lodging with Extended Amenities Guest Rooms Quantity", 0.8, 3.7)
  )
}

