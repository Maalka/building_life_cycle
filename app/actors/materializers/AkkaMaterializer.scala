package actors.materializers

import java.io.{PrintWriter, StringWriter}

import akka.actor.{Actor, ActorSystem}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

trait AkkaMaterializer {

  implicit val actorSystem: ActorSystem

  implicit val executionContext: ExecutionContext

  val decider: Supervision.Decider = {
    case NonFatal(th) =>

      val sw = new StringWriter
      th.printStackTrace(new PrintWriter(sw))
      // print this to stdout as well
      // TODO: Fix logging
      Console.println(sw.toString)
      Supervision.Resume

    case _ => Supervision.Stop
  }

  implicit val materializer: ActorMaterializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem).withSupervisionStrategy(decider)
  )
}
