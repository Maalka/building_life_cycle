package actors.flows

import javax.inject.Inject

import actors.validators.bedes.BedesDensityValidatorProps
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import com.maalka.bedes.BEDESTransformResult
import play.api.Configuration
import play.api.libs.json.Json

/**
  * Created by clayteeter on 7/14/17.
  */


class NonRefrigeratedWarehouseValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                                       override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  override val bedesUse = Seq("Non-Refrigerated Warehouse", "Warehouse-Unrefrigerated")

  val parentValidator = "Non-Refrigerated Warehouse"

  override val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 14, 274),
    //BedesValidationThreshold("Warehouse-Unrefrigerated Gross Area", 9992, 887014),
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 9992, 887014),
    BedesValidationThreshold("Warehouse-Unrefrigerated Business Average Weekly Hours", 40, 168)

  )

  override val densityFields = Seq(
    BedesValidationThreshold("Warehouse-Unrefrigerated Workers on Main Shift Quantity", 0.1, 1.5)
  )
}

