package actors.validators.bedes

import java.util.UUID

import actors.ValidatorActors.BedesValidators.{BedesValidator, BedesValidatorCompanion}
import actors.validators.Validator
import actors.validators.Validator.MapValid
import actors.validators.basic.{Exists, Numeric, WithinRange}
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future

object GrossFloorArea {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem): Props =
    Props(new GrossFloorArea(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class GrossFloorArea(guid: String,
                                       name: String,
                                       propertyId: String,
                                       validatorCategory: Option[String],
                                       override val arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem) extends BedesValidator {

  // the materializer to use.  this must be an ActorMaterializer

  implicit val materializer = ActorMaterializer()

  val validator = "bedeas_gross_floor_area"
  val bedesCompositeName = "Gross Floor Area"

  val componentValidators = Seq(propsWrapper(Numeric.props),
    propsWrapper(Exists.props, None),
    propsWrapper(WithinRange.props, Option(Json.obj("min" -> 0))))

  def isValid(refId: UUID, value: Option[Seq[BEDESTransformResult]]): Future[Validator.MapValid] = {
    log.debug("Validating Gross Floor Area: {}", value)
    sourceValidateFromComponents(value).map { results =>
      log.debug("Validated Gross Floor Area: {}", results)
      if (!results.head.valid || (results.head.valid && !results(1).valid)) {
        MapValid(valid = false, Option("Gross Floor Area is not a number"))
      } else if (results(1).valid && !results(1).valid) {
        MapValid(valid = false, Option("Gross Floor Area does not exist"))
      } else if (results(1).valid && !results(2).valid) {
        MapValid(valid = false, Option("Gross Floor Area is out of range: > 0"))
      } else {
        MapValid(valid = true, None)
      }
    }.runWith(Sink.head)
  }
}