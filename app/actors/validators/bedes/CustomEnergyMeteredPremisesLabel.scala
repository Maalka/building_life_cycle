package actors.validators.bedes

import java.util.UUID

import actors.ValidatorActors.BedesValidators.{BedesValidator, BedesValidatorCompanion}
import actors.validators.Validator
import actors.validators.Validator.MapValid
import actors.validators.basic.{EqualTo, Exists}
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.maalka.bedes.BEDESTransformResult
import org.joda.time.DateTime
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future

object CustomEnergyMeteredPremisesLabel extends BedesValidatorCompanion {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem): Props =
    Props(new CustomEnergyMeteredPremisesLabel(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class CustomEnergyMeteredPremisesLabel(guid: String,
                                           name: String,
                                           propertyId: String,
                                           validatorCategory: Option[String],
                                           override val arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem) extends BedesValidator {

  // the materializer to use.  this must be an ActorMaterializer

  implicit val materializer = ActorMaterializer()
  val validator = "bedes_custom_energy_metered_premises_label"
  val bedesCompositeName = "Custom Energy Metered Premises Label"

  val componentValidators = Seq(
    propsWrapper(Exists.props),
    propsWrapper(EqualTo.props, Option(Json.obj("expectedValue" -> "Whole Building"))))

  def isValid(refId: UUID, value: Option[Seq[BEDESTransformResult]]): Future[Validator.MapValid] = {
    sourceValidateFromComponents(value).map { results =>
      if (results.exists(_.valid == false)) {
        Validator.MapValid(valid = false, Option("Missing Whole Building Energy Use"))
      } else {
        Validator.MapValid(valid = true, None)
      }
    }.runWith(Sink.head)
  }
}