/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */

package actors.validators.basic

import java.util.UUID

import actors.CommonMessage
import actors.validators.Validator
import actors.validators.Validator.{MapValid, UpdateObjectValidatedDocument}
import akka.actor.{Actor, ActorLogging, PoisonPill}
import akka.stream.ActorMaterializer
import models._
import play.api.libs.json.JsObject

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.duration._

/**
  * Created by clayteeter on 3/28/17.
  */

trait BasicValidator[A] extends Actor with ActorLogging with Validator[A] {

  val guid: String
  val name: String
  val propertyId: String
  val arguments: Option[JsObject]
  val validator: String
  val validatorCategory: Option[String]

  implicit val materializer = ActorMaterializer()

  def isValid(refId: UUID, value: Option[A]):Future[MapValid]

  import play.api.libs.concurrent.Execution.Implicits._

  val timer = context.system.scheduler.scheduleOnce(60 seconds, self, PoisonPill)

  def validate(refId: UUID, value: Option[A]):Unit = {
    val future = for {
      valids <- isValid(refId, value)
    } yield {
      self forward UpdateObjectValidatedDocument(refId, guid, validator,
        validatorCategory, valid = valids.valid, value, None, valids.message, valids.details)
    }
    future.recover {
      case NonFatal(th) =>
        self forward UpdateObjectValidatedDocument(refId, guid, validator, validatorCategory,
          valid = false, value, None, Option(th.getMessage))
    }
  }


  def getValue(refId: UUID):Unit = throw new Exception("Not defined")
}
