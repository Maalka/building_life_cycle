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
import models._
import play.api.libs.json.JsObject

import scala.concurrent.Future

object EqualTo {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None): Props =
    Props(new EqualTo(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class EqualTo(guid: String,
                   name: String,
                   propertyId: String,
                   validatorCategory: Option[String],
                   override val arguments: Option[JsObject] = None) extends BasicValidator[MaalkaMeterData] {

  val validator = "validation_equal"

  def isValid(refId: UUID, value: Option[MaalkaMeterData]): Future[MapValid] = {
    Future {
      val expectedValue = arguments.map { arg => (arg \ "expectedValue").asOpt[String] }
      val expectedValueCase = arguments.map { arg => (arg \ "caseInsensitive").asOpt[Boolean] }
      val stringValue = value.flatMap { v =>
        expectedValueCase.map {
          case Some(true) => v.stringValue.map(_.toLowerCase())
          case Some(false) => v.stringValue
          case None => v.stringValue
        }
      }

      (stringValue, expectedValue) match {
        case (Some(a), Some(b)) if a == b =>  MapValid(valid = true, a)
        case (Some(a), _) => MapValid(valid = false, a)
        case _ => MapValid(valid = false, Option("Not Defined"))
      }
    }
  }

}