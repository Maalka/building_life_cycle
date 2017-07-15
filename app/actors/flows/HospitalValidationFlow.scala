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


case class HospitalValidationFlow @Inject()(validateFlow: ValidateFlow) {
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 211, 211),
    ("Gross Floor Area", 54000, 1426994))

  val densityFields = Seq[(String, Double, Double)](
    ("Health Care-Inpatient Hospital Workers on Main Shift Quantity", 1.2, 4.0),
    ("Health Care-Inpatient Hospital Staffed Beds Quantity", 0.2, 1.1),
    ("Health Care-Inpatient Hospital Medical Equipment Quantity", 0, 0.016)

    // enter fields here
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

