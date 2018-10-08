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

import actors.materializers.AkkaMaterializer
import actors.validators.Validator
import actors.validators.Validator.{MapValid, UpdateObjectValidatedDocument}
import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill}
import akka.stream.ActorMaterializer
import play.api.libs.json.JsObject

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.concurrent.duration._

trait BasicValidator[A] extends Actor with ActorLogging with Validator[A] with AkkaMaterializer {

  val guid: String
  val name: String
  val propertyId: String
  val arguments: Option[JsObject]
  val validator: String
  val validatorCategory: Option[String]

  def isValid(refId: UUID, value: Option[A]):Future[MapValid]

  val timer = context.system.scheduler.scheduleOnce(60 seconds, self, PoisonPill)

  def validate(refId: UUID, value: Option[A]):Unit = {
    val future = for {
      valids <- isValid(refId, value)
    } yield {
      self forward UpdateObjectValidatedDocument(refId, guid, validator, None,
        validatorCategory, valid = valids.valid, value, None, valids.message, valids.details)
    }
    future.recover {
      case NonFatal(th) =>
        self forward UpdateObjectValidatedDocument(refId, guid, validator, validatorCategory, None,
          valid = false, value, None, Option(th.getMessage))
    }
  }


  def getValue(refId: UUID):Unit = throw new Exception("Not defined")
}
