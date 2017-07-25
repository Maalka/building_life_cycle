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


class CourthouseValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                         override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  override val bedesUse = Seq("Courthouse")

  val parentValidator = "Courthouse"

  override val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 81, 335),
//    BedesValidationThreshold("Courthouse Gross Area", 10000, 490000),
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 10000, 490000),
    BedesValidationThreshold("Courthouse Business Average Weekly Hours", 40, 96)
  )

  override val densityFields = Seq(
    BedesValidationThreshold("Courthouse Workers on Main Shift Quantity", 0.4, 3.4),
    BedesValidationThreshold("Courthouse Computer Quantity", 0.5, 3.1)
  )
}

