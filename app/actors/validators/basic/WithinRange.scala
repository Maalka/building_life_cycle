package actors.validators.basic

import java.util.UUID

import actors.validators.Validator.MapValid
import akka.actor.Props
import models.MaalkaMeterData
import play.api.libs.json.JsObject

import scala.concurrent.Future

/**
  * Validates that MaterData includes 'usage' that is within range passed in as argument
  *
  * Created by clayteeter on 11/2/16.
  */

object WithinRange {
  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None): Props =
    Props(new WithinRange(guid, name, propertyId, validatorCategory, arguments))

}

/**
  * @param guid guid
  * @param name validator name
  * @param propertyId to document
  * @param validatorCategory to document
  * @param arguments - should include arguments 'min' and 'max' for range. Otherwise defaults are 0 and 100 respectively
  */
case class WithinRange(guid: String,
                       name: String,
                       propertyId: String,
                       validatorCategory: Option[String],
                       override val arguments: Option[JsObject] = None) extends BasicValidator[MaalkaMeterData] {

  val validator = "validation_within_range"


  import play.api.libs.concurrent.Execution.Implicits._

  def isValid(refId: UUID, value: Option[MaalkaMeterData]):Future[MapValid] = {
    Future {
      value.flatMap{ v => v.usage }.flatMap { v =>
        arguments.map { arg =>
          (arg \ "min").asOpt[Int] -> (arg \ "max").asOpt[Int] match {
            case (Some(l), Some(r)) if l < v && r > v => true
            case (Some(l), None) if l < v => true
            case (None, Some(r)) if r > v => true
            case (None, None) => false
          }
        }.map( (v, _))
      }.map {
        case (v, true) => MapValid(valid = true, Option(v.toString))
        case (v, false) => MapValid(valid = false, Option(v.toString))
      }.getOrElse(
        MapValid(valid = false, Option("Not Defined"))
      )
    }
  }
}


