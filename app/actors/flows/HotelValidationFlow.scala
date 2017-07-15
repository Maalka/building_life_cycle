package actors.flows

import javax.inject.Inject

import actors.validators.bedes.{BedesDensityValidator, BedesDensityValidatorProps}
import akka.NotUsed
import akka.stream.scaladsl._
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.Json

/**
  * Created by clayteeter on 7/14/17.
  */


case class HotelValidationFlow @Inject()(validateFlow: ValidateFlow){
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 89, 344),
    ("Lodging with Extended Amenities Gross Area", 21289, 617226))

  val densityFields = Seq[(String, Double, Double)](
    ("Lodging with Extended Amenities Workers on Main Shift Quantity", 0.1, 0.7),
    ("Lodging with Extended Amenities Commercial Refrigeration Quantity", 0, 0.15),
    ("Lodging with Extended Amenities Guest Rooms Quantity", 0.8, 3.7)
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

