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


case class MedicalOfficeValidationFlow @Inject()(validateFlow: ValidateFlow) {
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 84, 570),
    ("Health Care-Outpatient Non-diagnostic Gross Area", 7114, 202354))

  val densityFields = Seq[(String, Double, Double)](
    ("Health Care-Outpatient Non-diagnostic Business Average Weekly Hours", 40, 112),
    ("Health Care-Outpatient Non-diagnostic Workers on Main Shift Quantity", 0.8, 5)
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

