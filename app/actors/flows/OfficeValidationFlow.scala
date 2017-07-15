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


case class OfficeValidationFlow @Inject()(validateFlow: ValidateFlow) {
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 55, 470),
    ("Office Gross Area", 7381, 522173),
    ("Office Business Average Weekly Hours", 40, 105))

  val densityFields = Seq[(String, Double, Double)](

    ("Office Workers on Main Shift Quantity", 0.6, 5.5),
    ("Office Computer Quantity", 0.6, 6.5)
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

