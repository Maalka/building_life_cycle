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
import actors.validators.basic.{Exists, Length }
import akka.actor.{ActorSystem, Props}
import akka.stream.scaladsl.Sink
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future

object PremisesZIPCode extends BedesValidatorCompanion {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem): Props =
    Props(new PremisesZIPCode(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class PremisesZIPCode(guid: String,
                         name: String,
                         propertyId: String,
                         validatorCategory: Option[String],
                         override val arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem) extends BedesValidator {

  val validator = "bedes_premises_zip_code"
  val bedesCompositeName = "Premises ZIP Code"

  val componentValidators = Seq(
    propsWrapper(Exists.props),
    propsWrapper(Length.props, Option(Json.obj("expectedLength" -> 5))),
      propsWrapper(Length.props, Option(Json.obj("expectedLength" -> 10)))

  )

  def isValid(refId: UUID, value: Option[Seq[BEDESTransformResult]]): Future[Validator.MapValid] = {
    sourceValidateFromComponents(value).map {
      case results if results.headOption.exists(!_.valid) =>
        Validator.MapValid(valid = false, Option("Missing Premises ZIP Code"))
      case results if results.lift(1).exists(!_.valid) && results.lift(2).exists(!_.valid) =>
        Validator.MapValid(valid = false, Option("Premises Zip Code is not valid"))
      case results =>
        Validator.MapValid(valid = true, None)
    }.runWith(Sink.head)
  }
}