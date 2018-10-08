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
import actors.validators.basic.{Exists, WithinRange}
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future

object DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue extends BedesValidatorCompanion {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem): Props =
    Props(new DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue(guid: String,
                                            name: String,
                                            propertyId: String,
                                            validatorCategory: Option[String],
                                            override val arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem) extends BedesValidator {

  val validator = "bedes_delivered_and_generated_onsite_renewable_electricity_resource_value"
  val bedesCompositeName =
    "Delivered and Generated Onsite Renewable Electricity Resource Value"

  val componentValidators = Seq(
    propsWrapper(Exists.props),
    propsWrapper(WithinRange.props, Option(Json.obj("min" -> 0)))
  )

  def isValid(refId: UUID, value: Option[Seq[BEDESTransformResult]]): Future[Validator.MapValid] = {
    sourceValidateFromComponents(value).map {
      case results if results.headOption.exists(!_.valid) =>
        Validator.MapValid(valid = false, Option("No Electricity Use"))
      case results if results.lift(1).exists(!_.valid) =>
        Validator.MapValid(valid = false, Option("Electricity Use less then 0"))
      case results =>
        Validator.MapValid(valid = true, None)
    }.runWith(Sink.head)
  }
}