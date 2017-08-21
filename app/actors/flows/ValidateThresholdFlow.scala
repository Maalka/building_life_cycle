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

package actors.flows

import java.util.UUID

import actors.CommonMessage
import actors.ValidatorActors.BedesValidators.{BedesValidator, BedesValidatorCompanion}
import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes._
import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.pattern.ask
import akka.stream.scaladsl._
import javax.inject._

import akka.util.Timeout
import com.maalka.bedes.BEDESTransformResult
import play.api.Configuration
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

case class BedesValidationThreshold(bedesTerm: String, lower: Double, upper: Double)

abstract class ValidateThresholdFlow (implicit val actorSystem: ActorSystem,
                             implicit val configuration: Configuration) {

  private val bedesOccupencyTypeCompositeField =
    configuration.getString("maalka.bedesOccupencyTypeCompositeField").getOrElse("")

  val bedesUse: Seq[String]
  val rangeFields: Seq[BedesValidationThreshold]
  val densityFields: Seq[BedesValidationThreshold]
  val parentValidator: String

  lazy val validators = rangeFields.map { f =>
    BedesRangeValidatorProps(Option(Json.obj("compositeName" -> f.bedesTerm, "min" -> f.lower, "max" -> f.upper)))
  } ++ densityFields.map { f =>
    BedesDensityValidatorProps(Option(Json.obj("compositeName" -> f.bedesTerm, "min" -> f.lower, "max" -> f.upper)))
  }

  def run() = Flow.fromGraph(GraphDSL.create() { implicit builder =>
    // validators
    import GraphDSL.Implicits._


    implicit val timeout = Timeout(5 seconds)
    // only run validation of the primary type

    val in = builder.add(Flow[Seq[BEDESTransformResult]].map { brs =>
      brs.find(_.getCompositeName.contains(bedesOccupencyTypeCompositeField)).exists { br =>
        bedesUse.contains(br.getDataValue.getOrElse(""))
      } -> brs
    })

    var validate = builder.add(Flow[(Boolean, Seq[BEDESTransformResult])]
      .splitWhen(_ => true)
      .mapConcat {
        case (true, br) =>
          validators.map(_ -> br).to[scala.collection.immutable.Iterable]
        case _ =>
          scala.collection.immutable.Iterable.empty[(BedesValidatorCompanion, Seq[BEDESTransformResult])]
      }.mapAsync(1) {
        case (validator, br) =>
          actorSystem.actorOf(validator.props("Name", "", "", None, None)) ? Validator.Value(UUID.randomUUID(), Option(br))
      }
      .map {
        case CommonMessage.Failed(refId, message, cause) =>
          Left(cause)
        case d: UpdateObjectValidatedDocument =>
          Right(d.copy(parentValidator = Some(parentValidator)))
      }
      .fold(Seq.empty[Either[Throwable, UpdateObjectValidatedDocument]])(_ :+ _).mergeSubstreams)

    in ~> validate

    FlowShape(in.in, validate.out)
  })
}
