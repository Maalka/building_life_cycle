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


case class FinancialOfficeValidationFlow @Inject()(validateFlow: ValidateFlow) {

  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 98, 525),
    ("Office Gross Area", 10000, 1034547),
    ("Office Business Average Weekly Hours", 44, 130)
  )
  val densityFields = Seq[(String, Double, Double)](
    ("Office Workers on Main Shift Quantity", 0.4, 3.4),
    ("Office Computer Quantity", 0.7, 7.9)
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}


