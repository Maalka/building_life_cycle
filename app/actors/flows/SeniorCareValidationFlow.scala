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


case class SeniorCareValidationFlow @Inject()(validateFlow: ValidateFlow) {
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 103, 433),
    ("Health Care-Skilled Nursing Facility Gross Area", 16036, 230700))

  val densityFields = Seq[(String, Double, Double)](
    ("Health Care-Skilled Nursing Facility Workers on Main Shift Quantity", 0.2, 1.8),
    ("Health Care-Skilled Nursing Facility Computer Quantity", 0.1, 1.2),
    ("Health Care-Skilled Nursing Facility Commercial Refrigeration Quantity", 0, 0.2),
    ("Health Care-Skilled Nursing Facility Commercial Clothes Washer Quantity", 0, 0.09),
    ("Health Care-Skilled Nursing Facility Residential Clothes Washer Quantity", 0, 0.2),
    ("Health Care-Skilled Nursing Facility People Lift System Quantity", 0, 0.2),
    ("Health Care-Skilled Nursing Facility Guest Rooms Quantity", 0.7, 2.4)
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

