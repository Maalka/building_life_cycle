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


class ResidenceHallValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                            override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  val bedesUse = Seq("Residence Hall/Dormitory", "Lodging-Institutional")

  val parentValidator = "Residence Hall/Dormitory"

  val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 39, 311),
//    BedesValidationThreshold("Lodging-Institutional Gross Area", 6599, 320747))
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 6599, 320747)
  )


  val densityFields = Seq(
    BedesValidationThreshold("Lodging-Institutional Student Community Guest Rooms Quantity", 0.7, 4.9)
  )
}

