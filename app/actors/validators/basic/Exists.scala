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

object Exists {
  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None): Props =
    Props(new Exists(guid, name, propertyId, validatorCategory, arguments))

}
case class Exists(guid: String,
                  name: String,
                  propertyId: String,
                  validatorCategory: Option[String],
                  override val arguments: Option[JsObject] = None) extends BasicValidator[MaalkaMeterData] {

  import play.api.libs.concurrent.Execution.Implicits._

  val validator = "validation_exists"

  def isValid(refId: UUID, value: Option[MaalkaMeterData]): Future[MapValid] = {
    log.info("Running exists is valid: {}", value)
    Future {
      value match {
        case Some(a) if a.usage.isDefined || (a.stringValue.isDefined && !a.stringValue.contains("")) => MapValid(valid = true, Option("Valid"))
        case None => MapValid(valid = false, Option("Not Defined"))
      }
    }
  }
}


