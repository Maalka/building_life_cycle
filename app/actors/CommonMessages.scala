package actors

import java.util.UUID

import akka.actor.ActorRef

trait ActorMessage {
  val refId: UUID
}

trait PrivateMessage

object CommonMessage {
  case class Failed(refId: UUID, message: String, cause: Throwable) extends ActorMessage

  def log(refId: UUID, sender: ActorRef, self: ActorRef, message: String) = {
    "(%s) %s - from: %s to: %s".format(refId, message, sender, self.toString)
  }

  def implicitLog(refId: UUID, message: String)(implicit sender: ActorRef, self: ActorRef) = {
    log(refId, sender, self, message)
  }
}
