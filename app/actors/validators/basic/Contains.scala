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

object Contains {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None): Props =
    Props(new Contains(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class Contains(guid: String,
                    name: String,
                    propertyId: String,
                    validatorCategory: Option[String],
                    override val arguments: Option[JsObject] = None) extends BasicValidator[MaalkaMeterData] {

  val validator = "validation_contains"

  def isValid(refId: UUID, value: Option[MaalkaMeterData]): Future[MapValid] = {
    Future {
      val expectedValue = arguments.flatMap { arg => (arg \ "value").asOpt[String] }
      val expectedValueCase = arguments.flatMap { arg => (arg \ "caseInsensitive").asOpt[Boolean] }

      val stringValue = value.flatMap { v =>
        expectedValueCase.map {
          case true => v.stringValue.map(_.toLowerCase())
          case false => v.stringValue
        }.getOrElse(v.stringValue)
      }

      (stringValue, expectedValue) match {
        case (Some(a), Some(b)) if a.contains(b) =>  MapValid(valid = true, Some(a))
        case (Some(a), _) => MapValid(valid = false, Some(a))
        case _ => MapValid(valid = false, Option("Not Defined"))
      }
    }
  }

}