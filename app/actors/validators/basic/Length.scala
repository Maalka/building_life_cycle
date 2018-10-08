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
import scala.util.{Failure, Success, Try}
import scala.util.control.Exception.allCatch

/**
  * Validates that MaterData includes either 'usage' or 'stringValue' of correct length
  *
  */
object Length {
  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None): Props =
    Props(new Length(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid guid
  * @param name validator name
  * @param propertyId to document
  * @param validatorCategory to document
  * @param arguments should include param 'expectedLength' to compare to, otherwise default is 1
  */
case class Length(guid: String,
                  name: String,
                  propertyId: String,
                  validatorCategory: Option[String],
                  override val arguments: Option[JsObject] = None) extends BasicValidator[MaalkaMeterData] {

  val validator = "validation_length"
  def isValid(refId: UUID, value: Option[MaalkaMeterData]): Future[MapValid] = {
    Future {
      val length = arguments.flatMap { arg =>
        (arg \ "expectedLength").asOpt[String]
      }.map {
        case length => Try {
          length.toInt
        } match {
          case Success(l) => l
          case Failure(th) =>
            log.error(th, "unable to parse length agrument as int")
            0
        }
      } orElse  arguments.flatMap { arg =>
        (arg \ "expectedLength").asOpt[Int]
      } getOrElse(0)
      log.debug("Using arguments: {}", length)
      value match {
        case Some(a) =>
          (a.usage, a.stringValue) match {
            case (Some(usage), _) if usage.toString.length == length =>
              log.debug("Using usage value (valid): {}", usage.toString)
              MapValid(valid = true, Option(usage.toString))
            case (_, Some(stringValue)) if stringValue.length == length =>
              log.debug("Using string value (valid): {}", stringValue)
              MapValid(valid = true, Option(stringValue))
            case (Some(usage), _) =>
              log.debug("Using usage value (not valid): {}", usage)
              MapValid(valid = false, Option(usage.toString))
            case (_, Some(stringValue))  =>
              log.debug("Using string value (not valid): {}", stringValue)
              MapValid(valid = false, Option(stringValue))
            case _ =>
              log.debug("No value")
              MapValid(valid = false, Option("Not Defined"))
          }
        case None => MapValid(valid = false, Option("Not Defined"))
      }
    }

  }
}


