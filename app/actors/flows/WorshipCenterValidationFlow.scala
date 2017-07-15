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


case class WorshipCenterValidationFlow @Inject()(validateFlow: ValidateFlow) {
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 29, 228),
    ("Assembly-Religious Gross Area", 5136, 110000),
    ("Assembly-Religious Business Average Weekly Hours", 10, 98)
  )


  val densityFields = Seq[(String, Double, Double)](
    ("Assembly-Religious Computer Quantity", 0, 0.9),
    ("Assembly-Religious Capacity Quantity", 6, 51),
    ("Assembly-Religious Commercial Refrigeration Quantity", 0, 0.2)
    // enter fields here
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

