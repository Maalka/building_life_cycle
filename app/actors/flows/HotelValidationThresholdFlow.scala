package actors.flows

import javax.inject.Inject

import actors.validators.bedes.{BedesDensityValidator, BedesDensityValidatorProps}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import com.maalka.bedes.BEDESTransformResult
import play.api.Configuration
import play.api.libs.json.Json

/**
  * Created by clayteeter on 7/14/17.
  */


class HotelValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                    override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  val bedesUse = Seq("Hotel", "Lodging with extended amenities")

  val parentValidator = "Hotel"

  val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 89, 344),
    //BedesValidationThreshold("Lodging with Extended Amenities Gross Area", 21289, 617226))
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 21289, 617226)
  )


  val densityFields = Seq(
    BedesValidationThreshold("Lodging with Extended Amenities Workers on Main Shift Quantity", 0.1, 0.7),
    BedesValidationThreshold("Lodging with Extended Amenities Commercial Refrigeration Quantity", 0, 0.15),
    BedesValidationThreshold("Lodging with Extended Amenities Guest Rooms Quantity", 0.8, 3.7)
  )
}

