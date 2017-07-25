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


class OfficeValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                     override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  override val bedesUse = Seq("Office")

  val parentValidator = "Office"

  override val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 55, 470),
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 7381, 522173),

//    BedesValidationThreshold("Office Gross Area", 7381, 522173),
    BedesValidationThreshold("Office Business Average Weekly Hours", 40, 105))

  override val densityFields = Seq(

    BedesValidationThreshold("Office Workers on Main Shift Quantity", 0.6, 5.5),
    BedesValidationThreshold("Office Computer Quantity", 0.6, 6.5)
  )
}


