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

/**
  * Created by clayteeter on 7/14/17.
  */


class SeniorCareValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                         override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  val bedesUse = Seq("Senior Care Community", "Skilled nursing facility")

  val parentValidator = "Senior Care Community"

  val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 103, 433),
    //BedesValidationThreshold("Health Care-Skilled Nursing Facility Gross Area", 16036, 230700)),
  BedesValidationThreshold("EPA Calculated Gross Floor Area", 16036, 230700))


  val densityFields = Seq(
    BedesValidationThreshold("Health Care-Skilled Nursing Facility Workers on Main Shift Quantity", 0.2, 1.8),
    BedesValidationThreshold("Health Care-Skilled Nursing Facility Computer Quantity", 0.1, 1.2),
    BedesValidationThreshold("Health Care-Skilled Nursing Facility Commercial Refrigeration Quantity", 0, 0.2),
    BedesValidationThreshold("Health Care-Skilled Nursing Facility Commercial Clothes Washer Quantity", 0, 0.09),
    BedesValidationThreshold("Health Care-Skilled Nursing Facility Residential Clothes Washer Quantity", 0, 0.2),
    BedesValidationThreshold("Health Care-Skilled Nursing Facility People Lift System Quantity", 0, 0.2),
    BedesValidationThreshold("Health Care-Skilled Nursing Facility Guest Rooms Quantity", 0.7, 2.4)
  )
}

