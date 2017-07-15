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

case class BedesRangeValidatorProps(arguments: Option[JsObject] = None) extends BedesValidatorCompanion {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            _arguments: Option[JsObject])(implicit actorSystem: ActorSystem): Props =
    Props(BedesRangeValidator(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class BedesRangeValidator(guid: String,
                                 name: String,
                                 propertyId: String,
                                 validatorCategory: Option[String],
                                 override val arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem) extends BedesValidator {

  // the materializer to use.  this must be an ActorMaterializer

  implicit val materializer = ActorMaterializer()

  val validator = "bedes_range_validator"
  override val bedesCompositeName: String = arguments.flatMap { arg =>
    (arg \ "compositeName").asOpt[String]
  }.get

  val min: Option[Long] = arguments.flatMap { arg =>
    (arg \ "min").asOpt[Long] orElse (arg \ "min").asOpt[String].map(_.toLong)
  }
  val max: Option[Long] = arguments.flatMap { arg =>
    (arg \ "max").asOpt[Long] orElse (arg \ "max").asOpt[String].map(_.toLong)
  }

  val componentValidators = Seq(
    propsWrapper(Numeric.props),
    propsWrapper(Exists.props),
    propsWrapper(WithinRange.props, Option(Json.obj("min" -> min, "max" -> max)))
  )


  def isValid(refId: UUID, value: Option[Seq[BEDESTransformResult]]): Future[Validator.MapValid] = {
    sourceValidateFromComponents(value).map {
      case results if !results.head.valid =>
        MapValid(valid = false, Option("%s is not a number".format(bedesCompositeName)))
      case results if results.lift(1).exists(!_.valid) =>
        MapValid(valid = false, Option("%s is missing".format(bedesCompositeName)))
      case results if results.lift(2).exists(!_.valid) =>
        MapValid(valid = false, Option("%s out of range (%s - %s)".format(bedesCompositeName, min, max)))
      case results =>
        Console.println(results)
        MapValid(valid = true, None)
    }.runWith(Sink.head)
  }
}