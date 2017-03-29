package actors.validators.bedes

import java.util.UUID

import actors.ValidatorActors.BedesValidators.{BedesValidator, BedesValidatorCompanion}
import actors.validators.Validator
import actors.validators.basic.{WithinRange, Exists}
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future

object DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue extends BedesValidatorCompanion {

  def props(guid: String,
            name: String,
            propertyId: String,
            validatorCategory: Option[String],
            arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem): Props =
    Props(new DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue(guid, name, propertyId, validatorCategory, arguments))
}

/**
  * @param guid - guid
  * @param name - validator name
  * @param propertyId - to document
  * @param validatorCategory - to document
  * @param arguments - should include argument 'expectedValue' that will be used for comparision
  */
case class DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue(guid: String,
                                            name: String,
                                            propertyId: String,
                                            validatorCategory: Option[String],
                                            override val arguments: Option[JsObject] = None)(implicit actorSystem: ActorSystem) extends BedesValidator {

  // the materializer to use.  this must be an ActorMaterializer

  implicit val materializer = ActorMaterializer()
  val validator = "bedes_delivered_and_generated_onsite_renewable_electricity_resource_value"
  val bedesCompositeName = "Delivered And Generated Onsite Renewable Electricity Resource Value"

  val componentValidators = Seq(propsWrapper(Exists.props),
    propsWrapper(WithinRange.props, Option(Json.obj("min" -> 0))))

  def isValid(refId: UUID, value: Option[Seq[BEDESTransformResult]]): Future[Validator.MapValid] = {
    sourceValidateFromComponents(value).map { results =>
      if (results.exists(_.valid == false)) {
        Validator.MapValid(valid = false, Option("Built before 1800"))
      } else {
        Validator.MapValid(valid = true, None)
      }
    }.runWith(Sink.head)
  }
}