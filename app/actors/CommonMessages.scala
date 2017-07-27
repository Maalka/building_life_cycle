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
