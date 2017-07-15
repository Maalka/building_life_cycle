package actors.flows

import actors.validators.bedes.BedesDensityValidatorProps
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import javax.inject._
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.Json

/**
  * Created by clayteeter on 7/14/17.
  */

class RetailValidationFlow @Inject()(validateFlow: ValidateFlow){
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 69, 400),
    ("Retail-Dry Goods Retail Gross Area", 7016, 127455),
    ("Retail-Dry Goods Retail Business Average Weekly Hours", 71, 168)
  )

  val densityFields = Seq[(String, Double, Double)](
    ("Retail-Dry Goods Retail Workers on Main Shift Quantity", 0.1, 1),
    ("Retail-Dry Goods Retail Cash Register Quantity", 0.1, 0.8),
    ("Retail-Dry Goods Retail Computer Quantity", 0.0, 0.8),
    ("Retail-Dry Goods Retail Refrigeration Walk-in Quantity", 0, 0.1),
    ("Retail-Dry Goods Retail Commercial Refrigeration Case Quantity", 0, 1)
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

