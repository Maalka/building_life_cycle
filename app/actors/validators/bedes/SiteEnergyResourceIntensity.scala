package actors.validators.bedes

import java.util.UUID

import actors.ValidatorActors.BedesValidators.{BedesValidator, BedesValidatorCompanion}
import actors.validators.Validator
import actors.validators.Validator.MapValid
import actors.validators.basic.{ WithinRange, Numeric }
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future

object SiteEnergyResourceIntensity extends BedesValidatorCompanion {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem): Props =
    Props(new SiteEnergyResourceIntensity(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class SiteEnergyResourceIntensity(guid: String,
                                  name: String,
                                  propertyId: String,
                                  validatorCategory: Option[String],
                                  override val arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem) extends BedesValidator {

  // the materializer to use.  this must be an ActorMaterializer

  implicit val materializer = ActorMaterializer()

  val validator = "bedeas_site_energy_resource_intensity"
  val bedesCompositeName = "Site Energy Resource Intensity"

  val componentValidators = Seq(propsWrapper(Numeric.props),
    propsWrapper(WithinRange.props, Option(Json.obj("min" -> 0))),
    propsWrapper(WithinRange.props, Option(Json.obj("min" -> 40, "max" -> 375))),
    propsWrapper(WithinRange.props, Option(Json.obj("min" -> 375))))

  def isValid(refId: UUID, value: Option[Seq[BEDESTransformResult]]): Future[Validator.MapValid] = {
    sourceValidateFromComponents(value).map { results =>
      if (!results.head.valid || (results.head.valid && !results(1).valid)) {
        MapValid(valid = false, Option("Site energy use intensity (EUI) metrics"))
      } else if (results(1).valid && !results(2).valid && !results(3).valid) {
        MapValid(valid = false, Option("Unusually low energy use by square foot"))
      } else if (results(1).valid && !results(2).valid && results(3).valid) {
        MapValid(valid = false, Option("Unusually high energy use by square foot"))
      } else {
        MapValid(valid = true, None)
      }
    }.runWith(Sink.head)
  }
}