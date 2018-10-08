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

package actors.validators.bedes

import java.util.UUID

import actors.ValidatorActors.BedesValidators.{BedesValidator, BedesValidatorCompanion}
import actors.validators.Validator
import actors.validators.Validator.MapValid
import actors.validators.basic.{Exists, Numeric, WithinRange}
import akka.actor.{ActorSystem, Props}
import akka.stream.scaladsl.Sink
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future

object EnergyStarScoreAssessmentValue extends BedesValidatorCompanion {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem): Props =
    Props(new EnergyStarScoreAssessmentValue(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class EnergyStarScoreAssessmentValue(guid: String,
                          name: String,
                          propertyId: String,
                          validatorCategory: Option[String],
                          override val arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem) extends BedesValidator {

  val validator = "bedeas_energy_star_score_assessment_value"
  val bedesCompositeName = "ENERGY STAR Score Assessment Value"
  var useTypes = Seq("Bank branch",
    "Barracks", "Lodging-Institutional",
    "Courthouse",
    "Data center",
    "Distribution Center", "Warehouse-Unrefrigerated",
    "Financial Office", "Office",
    "Hospital (general medical & surgical)", "Health care-Inpatient hospital",
    "Hotel", "Lodging with extended amenities",
    "K-12 School", "Education",
    "Medical Office", "Health care-Outpatient non-diagnostic",
    "Multifamily Housing", "Multifamily",
    "Non-Refrigerated Warehouse", "Warehouse-Unrefrigerated",
    "Office",
    "Refrigerated Warehouse", "Warehouse-Refrigerated",
    "Residence Hall/Dormitory", "Lodging-Institutional",
    "Retail Store", "Retail-Dry goods retail",
    "Senior Care Community", "Skilled nursing facility",
    "Supermarket/Grocery Store", "Food sales-Grocery store",
    "Wastewater Treatment Plant", "Water treatment-Wastewater",
    "Wholesale Club/Supercenter", "Retail-Hypermarket",
    "Worship Facility", "Assembly-Religious")


  val min:Option[Double] = Some(1)
  val max: Option[Double] = Some(1000)

  val componentValidators = Seq(propsWrapper(Numeric.props),
    propsWrapper(Exists.props, None),
    propsWrapper(WithinRange.props, Option(Json.obj("min" -> min, "max" -> max))))

  def isValid(refId: UUID, value: Option[Seq[BEDESTransformResult]]): Future[Validator.MapValid] = {
    log.debug("ENERGY STAR Score Assessment Value: {}", value)
    value.map { tr =>
      tr.find(_.getCompositeName.contains("Calculated Primary Occupancy Classification")).flatMap(_.getDataValue) ->
        tr.find(_.getCompositeName.contains("ENERGY STAR Score Assessment Value"))
    }.filter(_._1.isDefined).flatMap {
      case (Some(use: String), tr) if useTypes.contains(use) =>
        log.debug("Use and TR: {} {}", use, tr)
        tr
      case _ => None
    } match {
      case Some(tr) =>
        log.debug("Use and TR: {}", tr)
        sourceValidateFromComponents(Option(Seq(tr))).map {
        case results if !results.head.valid =>
          MapValid(valid = false, Option("ENERGY STAR Score Assessment Value is not a number"))
        case results if results.lift(1).exists(!_.valid) =>
          MapValid(valid = false, Option("ENERGY STAR Score Assessment Value is missing"))
        case results if results.lift(2).exists(!_.valid) =>
          formatMapValidRangeResponse(bedesCompositeName, min, max)
        case results =>
          MapValid(valid = true, None)
      }.runWith(Sink.head)

      case None => Future(MapValid(valid = true, None))
    }
  }
}