/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */

package controllers

import java.io.{PrintWriter, StringWriter}
import java.util.UUID

import actors.CommonMessage
import actors.flows.BankValidationThresholdFlow
import actors.flows._
import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.stream._
import com.google.inject.Inject
import play.api.mvc._
import com.maalka.bedes.{BEDESTransform, BEDESTransformResult}
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.libs.json.{JsString, Json}
/**
  * Created by clayteeter on 3/28/17.
  */
class DataQuality @Inject()(
                             implicit actorSystem: ActorSystem,
                             validateFlow: ValidateFlow
                            ) extends Controller {

  def validate = Action.async(parse.multipartFormData) { request =>

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

    implicit val materializer = ActorMaterializer(
      ActorMaterializerSettings(actorSystem).withSupervisionStrategy(decider)
    )

    request.body.file("inputData").map { file =>
      Source.fromIterator(() => BEDESTransform.fromXLS(None, file.ref.file, None, None))
        .groupBy(10000, _._1.propertyId.get)
          .log("Validating Property")
        .fold(Seq.empty[BEDESTransformResult])(_ :+ _._1)
        .mergeSubstreams
        .via(validateFlow.run)
        .map(_.map(_.map(_.right.get)))
        .runWith(Sink.seq) map { res =>
        Ok(Json.obj(
          "result" -> Json.toJson(res.map(Json.toJson(_))),
          "status" -> JsString("OK")
        ))
      }
    }.getOrElse {
      Future(Ok("Failure"))
    }
  }
}

