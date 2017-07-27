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

package actors.validators.basic

import java.util.UUID

import akka.actor.Props
import models.MaalkaMeterData
import play.api.libs.json.JsObject
import actors.validators.Validator.MapValid

import scala.concurrent.Future
import scala.util.control.Exception.allCatch

/**
  * Validates that MaterData includes 'stringValue' that is equal to a String passed in arguments
  *
  * */

object Numeric {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None): Props =
    Props(new Numeric(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class Numeric(guid: String,
                    name: String,
                    propertyId: String,
                    validatorCategory: Option[String],
                    override val arguments: Option[JsObject] = None) extends BasicValidator[MaalkaMeterData] {

  import play.api.libs.concurrent.Execution.Implicits._

  val validator = "validation_numeric"

  def isValid(refId: UUID, value: Option[MaalkaMeterData]): Future[MapValid] = {
    log.debug("Validation Numaric: {}", value)
    Future {
      value match {
        case Some(a) =>
          (a.usage, a.stringValue) match {
            case (Some(usage), _) => MapValid(valid = true, Option(usage.toString))

            // This doesn't seem right.  If a string value is a string value then it is not a numeric value, even if it
            // can be cast to a double.
            case (_, Some(usage)) if (allCatch opt usage.toDouble).isDefined => MapValid(valid = true, Option(usage))

            case (_, Some(usage)) =>
              log.debug("Not a number: {}", usage)
                MapValid(valid=false, Option(usage))
            case _ => MapValid(valid = false, Option("Not Defined"))
          }
        case None => MapValid(valid = false, Option("Not Defined"))
      }
    }
  }
}