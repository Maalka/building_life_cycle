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

import actors.validators.Validator.MapValid
import akka.actor.Props
import models.MaalkaMeterData
import play.api.libs.json.JsObject

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Validates that MaterData includes 'usage' that is within range passed in as argument
  *
  */

object WithinRange {
  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None): Props =
    Props(new WithinRange(guid, name, propertyId, validatorCategory, arguments))

}

/**
  * @param guid guid
  * @param name validator name
  * @param propertyId to document
  * @param validatorCategory to document
  * @param arguments - should include arguments 'min' and 'max' for range. Otherwise defaults are 0 and 100 respectively
  */
case class WithinRange(guid: String,
                       name: String,
                       propertyId: String,
                       validatorCategory: Option[String],
                       override val arguments: Option[JsObject] = None) extends BasicValidator[MaalkaMeterData] {

  val validator = "validation_within_range"

  def isValid(refId: UUID, value: Option[MaalkaMeterData]):Future[MapValid] = {
    log.debug("Found value: {}", value)
    Future {
      value.flatMap{ v => v.usage }.flatMap { v =>
        arguments.map { arg =>
          val min = (arg \ "min").asOpt[Double] orElse (arg \ "min").asOpt[String].map(_.toDouble)
          val max = (arg \ "max").asOpt[Double] orElse (arg \ "max").asOpt[String].map(_.toDouble)

          val g = MapValid(true, Option(v.toString))
          log.debug("Arguments: min {} max {} value {}", min, max, value)
          (min, max) match {
            case (Some(l), Some(r)) if v > l && v < r => true
            case (Some(l), None) if v > l => true
            case (None, Some(r)) if v < r => true
            case (None, None) => false
            case _ => false
          }

        }.map( (v, _))
      }.map {
        case (v, true) =>
          val g = MapValid(true, Option(v.toString))
          MapValid(true, Option(v.toString))
        case (v, false) =>
          MapValid(false, Option(v.toString))
      }.getOrElse(
        MapValid(valid = false, Option("Not Defined"))
      )
    }
  }
}


