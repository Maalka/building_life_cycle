package controllers

import play.api.mvc.Action
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

class StaticController extends Controller {

  def type1() = Action {
    Ok(views.html.alpacaTest1())
  }

  def type2() = Action {
    Ok(views.html.alpacaTest2())
  }
  def type3() = Action {
    Ok(views.html.alpacaTest3())
  }
  def type4() = Action {
    Ok(views.html.alpacaTest4())
  }
  def type5() = Action {
    Ok(views.html.alpacaTest5())
  }
  def type6() = Action {
    Ok(views.html.alpacaTest6())
  }
}
