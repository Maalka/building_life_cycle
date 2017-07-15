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


case class CourthouseValidationFlow @Inject()(validateFlow: ValidateFlow) {

  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 81, 335),
    ("Courthouse Gross Area", 10000, 490000),
    ("Courthouse Business Average Weekly Hours", 40, 96)
  )
  val densityFields = Seq[(String, Double, Double)](
    ("Courthouse Workers on Main Shift Quantity", 0.4, 3.4),
    ("Courthouse Computer Quantity", 0.5, 3.1)
  )


  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

