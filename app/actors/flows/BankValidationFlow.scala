package actors.flows

import javax.inject.Inject

import actors.validators.bedes.BedesDensityValidatorProps
import akka.NotUsed
import akka.stream.scaladsl._
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.Json

/**
  * Created by clayteeter on 7/14/17.
  */


case class BankValidationFlow @Inject()(validateFlow: ValidateFlow) {
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 100, 512),
    ("Bank Gross Area", 2000, 16403))
    ("Bank Business Average Weekly Hours", 41, 77)

  val densityFields = Seq[(String, Double, Double)](
    ("Bank Workers on Main Shift Quantity", 0.8, 4.2),
    ("Bank Computer Quantity", 0.7, 5)
    // enter fields here
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

