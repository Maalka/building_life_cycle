package actors.validators.basic

import java.util.UUID

import actors.validators.Validator.MapValid
import akka.actor.Props
import models._
import play.api.libs.json.JsObject

import scala.concurrent.Future

object EqualTo {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None): Props =
    Props(new EqualTo(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class EqualTo(guid: String,
                   name: String,
                   propertyId: String,
                   validatorCategory: Option[String],
                   override val arguments: Option[JsObject] = None) extends BasicValidator[MaalkaMeterData] {

  import play.api.libs.concurrent.Execution.Implicits._

  val validator = "validation_equal"

  def isValid(refId: UUID, value: Option[MaalkaMeterData]): Future[MapValid] = {
    Future {
      val expectedValue = arguments.map { arg => (arg \ "expectedValue").asOpt[String] }
      val expectedValueCase = arguments.map { arg => (arg \ "caseInsensitive").asOpt[Boolean] }
      val stringValue = value.flatMap { v =>
        expectedValueCase.map {
          case Some(true) => v.stringValue.map(_.toLowerCase())
          case Some(false) => v.stringValue
          case None => v.stringValue
        }
      }

      (stringValue, expectedValue) match {
        case (Some(a), Some(b)) if a == b =>  MapValid(valid = true, a)
        case (Some(a), _) => MapValid(valid = false, a)
        case _ => MapValid(valid = false, Option("Not Defined"))
      }
    }
  }

}