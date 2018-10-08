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

package actors.validators

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill}
import java.util.UUID

import actors.{ActorMessage, CommonMessage}
import actors.CommonMessage.Failed
import org.joda.time.DateTime
import play.api.libs.json.{Json, Writes}
import play.api.libs.json.JodaWrites._

import scala.concurrent.ExecutionContextExecutor

object Validator {
  trait ValidationCategory {}


  case object DataValidationCategory extends ValidationCategory
  case object ComplianceValidationCategory extends ValidationCategory

  case class MapValid(valid:Boolean,
                      message: Option[String],
                      details: Seq[ValidatedDocumentDetails] = Seq(),
                      value: Option[String] = None,
                      dataCategory: Option[ValidationCategory] = None
                     )
  case class Value(refId: UUID, value: Option[Any])
  case class Validate(refId: UUID, value: Option[Any] = None) extends ActorMessage
  case class Validated(refId: UUID) extends ActorMessage
  case class ValidatedDocumentDetails(valid: Boolean, message: Option[String])

  implicit val validatedDocumentDetailFormatter = Json.format[ValidatedDocumentDetails]
  case class UpdateObjectValidatedDocument(refId: UUID,
                                           validatorKey: String,
                                           validator: String,
                                           validatorCategory: Option[String],
                                           parentValidator: Option[String] = None,
                                           valid: Boolean,
                                           value: Option[Any],
                                           valueType: Option[String] = None,
                                           message: Option[String] = None,
                                           details: Seq[ValidatedDocumentDetails] = Seq()
                                          )

  def writes(obj: UpdateObjectValidatedDocument) = {
    Json.obj(
      ("refId", Json.toJson(obj.refId)),
      ("validatorKey", Json.toJson(obj.validatorKey)),
      ("validator", Json.toJson(obj.validator)),
      ("parentValidator", Json.toJson(obj.parentValidator)),
      ("validatorCategory", Json.toJson(obj.validatorCategory)),
      ("valid", Json.toJson(obj.valid)),
      ("value", obj.value.collect {
        case obj: Long => Json.toJson(obj)
        case obj: String => Json.toJson(obj)
        case obj: Double => Json.toJson(obj)
        case obj: Float => Json.toJson(obj)
        case obj: Int => Json.toJson(obj)
        case obj: DateTime => Json.toJson(obj)
      }),
      ("valueType", Json.toJson(obj.valueType)),
      ("message", Json.toJson(obj.message)),
      ("details", Json.toJson(obj.details))
    )

  }

  implicit val validatedDocumentDetails = Json.format[ValidatedDocumentDetails]
  implicit val updateObjectValidatedDocumentFormat = Writes[UpdateObjectValidatedDocument](writes)

}

// TODO: Refactor this to ValidateActor
trait Validator[A] extends Actor with ActorLogging{

  import Validator._

  implicit val actorSystem: ActorSystem = context.system

  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  val propertyId: String

  var originalSender:ActorRef = _

  def getValue(refId: UUID)
  def validate(refId: UUID, value: Option[A]):Unit

  //
  var save: Boolean = true
  val logger = log


  def receive: Receive = {

    case Validate(refId: UUID, value: Option[A]) =>
      logger.debug(CommonMessage.log(refId, sender, self, "Received Validate"))
      originalSender = sender
      if (value.isDefined) {
        self ! Value(refId, value)
      } else {
        getValue(refId)
      }

    // The value to validate
    case Value(refId, value: Option[A]) =>
      originalSender = sender
      validate(refId, value)

    case Failed(refId, message, cause) =>
      logger.error(cause, CommonMessage.log(refId, sender, self, "Received Failed: %s".format(message)))

      originalSender ! Failed(refId, message, cause)
      self ! PoisonPill

    case updateDocument:UpdateObjectValidatedDocument =>
      logger.debug(CommonMessage.log(updateDocument.refId, sender, self, "Received UpdateObjectValidatedDocument"))
      originalSender ! updateDocument
      self ! PoisonPill
  }
}