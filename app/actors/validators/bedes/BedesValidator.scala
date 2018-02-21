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

package actors.ValidatorActors.BedesValidators

import java.io.{PrintWriter, StringWriter}
import java.util.UUID

import actors.CommonMessage.Failed
import actors.validators.Validator.MapValid
import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.ask
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.Source
import com.maalka.bedes.BEDESTransformResult
import models.MaalkaMeterData
import org.joda.time.DateTime
import play.api.libs.json.JsObject

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

trait BedesValidatorCompanion {
  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem): Props
}

trait BedesValidator extends Actor with ActorLogging with Validator[Seq[BEDESTransformResult]] {

  implicit val timeout = akka.util.Timeout(5 seconds)
  val decider: Supervision.Decider = {
    case NonFatal(th) =>

      val sw = new StringWriter
      th.printStackTrace(new PrintWriter(sw))
      // print this to stdout as well
      // TODO: Fix loging
      Console.println(sw.toString)
      Supervision.Resume

    case _ => Supervision.Stop
  }

  val guid: String
  val name: String
  val propertyId: String
  val arguments: Option[JsObject]
  val validator: String
  val validatorCategory: Option[String]


  def propsWrapper(props: ((String, String, String, Option[String], Option[JsObject]) => Props), args: Option[JsObject] = None) =
    (guid: String, name: String, propertyId: String, validatorCategory: Option[String], injectArgs: Option[JsObject]) => {
      val useArgs = (args, injectArgs) match {
        case (Some(a), Some(i)) => Option(a ++ i)
        case (None, Some(i)) => Option(i)
        case (Some(a), None) => Option(a)
        case _ => None
      }
      props(guid, name, propertyId, validatorCategory, useArgs)
    }

  case class PropWrapperOptions(guid: String, name: String, propertyId: String,
                                validatorCategory: Option[String],
                                injectArgs: Option[JsObject])

  def transformResultToMeterData(transformResult: BEDESTransformResult): Option[MaalkaMeterData] = {
    val meterData = transformResult.getData.toSeq collect {
      case (key, Some(value: DateTime)) =>
        log.debug("Transforming datetime: {}", value)
        MaalkaMeterData(None, None, "",
          Option(transformResult.startTime), transformResult.endTime, usage = Option(value.getMillis),
          cost = None, estimatedValue = None)
      case (key, Some(value: Long)) =>
        log.debug("Transforming long: {}", value)
        MaalkaMeterData(None, None, "",
          Option(transformResult.startTime), transformResult.endTime, usage = Option(value),
          cost = None, estimatedValue = None)

      case (key, Some(value: Int)) =>
        log.debug("Transforming int: {}", value)
        MaalkaMeterData(None, None, "",
          Option(transformResult.startTime), transformResult.endTime, usage = Option(value),
          cost = None, estimatedValue = None)
      case (key, Some(value: Double)) =>
        log.debug("Transforming double: {}", value)
        MaalkaMeterData(None, None, "",
          Option(transformResult.startTime), transformResult.endTime, usage = Option(value),
          cost = None, estimatedValue = None)
      case (key, Some(value: String)) =>
        MaalkaMeterData(None, None, "",
          Option(transformResult.startTime), transformResult.endTime, usage = None, stringValue = Option(value),
          cost = None, estimatedValue = None)
    }
    meterData.headOption
  }

  def isValid(refId: UUID, value: Option[Seq[BEDESTransformResult]]): Future[MapValid]

  val bedesCompositeName: String
  val componentValidators: Seq[(String, String, String, Option[String], Option[JsObject]) => Props]

  def getValue(refId: UUID):Unit = throw new Exception("Not defined")

  def validate(refId: UUID, value: Option[Seq[BEDESTransformResult]]):Unit = {
    val future = for {
      valids <- isValid(refId, value)
    } yield {
      val ov = value.flatMap{_.find(_.getCompositeName.contains(bedesCompositeName))}.flatMap { v =>
        v.getData.map{ d => (d, v.getDataType)}.headOption
      }.flatMap { case (v, d) =>
        v._2.map(_ -> d)
      }
      self forward UpdateObjectValidatedDocument(refId, validator, bedesCompositeName, None,
        validatorCategory, valid = valids.valid, ov.map(_._1), ov.flatMap(_._2),
        valids.message, valids.details)
    }
    future.recover {
      case NonFatal(th) =>
        log.error(th, "Error Validating")
        self forward UpdateObjectValidatedDocument(refId, validator,
          bedesCompositeName, None, validatorCategory,
          valid = false, Option(th.getMessage))
    }
  }

  def sourceValidateFromComponents(value: Option[Seq[BEDESTransformResult]]) = {
    Source.fromIterator { () => componentValidators.toIterator }.mapAsync(1) { validator =>
      val jobId = UUID.randomUUID()
      val actor = context.actorOf(validator(guid, name, propertyId, validatorCategory, None))
      val md = Try {
        value.flatMap(_.find(_.getCompositeName.contains(bedesCompositeName)))
          .flatMap(transformResultToMeterData)
      } match {
        case Success(v) => v
        case Failure(th) =>
          log.error(th, "Error")
          throw th
      }
      actor ? Validator.Value(jobId, md)

    }.fold(Seq.empty[UpdateObjectValidatedDocument]) {
      case (l, r: UpdateObjectValidatedDocument) => l :+ r
      case (l, Failed(_, message, th)) =>
        logger.error(th, "Unable to validate component: %s".format(message))
        l
    }
  }

  def formatMapValidRangeResponse(bedesCompositeName: String, min: Option[Double], max: Option[Double]): MapValid = {
    val formatter = java.text.NumberFormat.getIntegerInstance
    if (min.isDefined && max.isDefined) {
      MapValid(valid = false, Option("%s out of range (%s - %s)".format(bedesCompositeName,
        formatter.format(min.get),
        formatter.format(max.get))))
    } else if (min.isDefined && max.isEmpty) {
      MapValid(valid = false, Option("%s is not greater then %s".format(bedesCompositeName,
        formatter.format(min.get))))
    } else if (min.isEmpty && max.isDefined) {
      MapValid(valid = false, Option("%s is not less then %s".format(bedesCompositeName,
        formatter.format(max.get))))
    } else {
      MapValid(valid = false, Option("%s: No min or max defined in validator"))
    }
  }
}

