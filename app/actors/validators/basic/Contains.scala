package actors.validators.basic

import java.util.UUID

import akka.actor.Props
import models.MaalkaMeterData
import play.api.libs.json.JsObject
import actors.validators.Validator.MapValid


import scala.concurrent.Future

/**
  * Validates that MaterData includes 'stringValue' that is equal to a String passed in arguments
  *
  * Created by tadassugintas on 2016-12-13.
  */

object Contains {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None): Props =
    Props(new Contains(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class Contains(guid: String,
                    name: String,
                    propertyId: String,
                    validatorCategory: Option[String],
                    override val arguments: Option[JsObject] = None) extends BasicValidator[MaalkaMeterData] {

  import play.api.libs.concurrent.Execution.Implicits._

  val validator = "validation_contains"

  def isValid(refId: UUID, value: Option[MaalkaMeterData]): Future[MapValid] = {
    Future {
      val expectedValue = arguments.flatMap { arg => (arg \ "value").asOpt[String] }
      val expectedValueCase = arguments.flatMap { arg => (arg \ "caseInsensitive").asOpt[Boolean] }

      val stringValue = value.flatMap { v =>
        expectedValueCase.map {
          case true => v.stringValue.map(_.toLowerCase())
          case false => v.stringValue
        }.getOrElse(v.stringValue)
      }

      (stringValue, expectedValue) match {
        case (Some(a), Some(b)) if a.contains(b) =>  MapValid(valid = true, Some(a))
        case (Some(a), _) => MapValid(valid = false, Some(a))
        case _ => MapValid(valid = false, Option("Not Defined"))
      }
    }
  }

}