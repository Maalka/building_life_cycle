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


case class SupermarketValidationFlow @Inject()(validateFlow: ValidateFlow) {
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 328, 828),
    ("Food Sales-Grocery Store Gross Area", 19785, 92961),
    ("Food Sales-Grocery Store Business Average Weekly Hours", 98, 164)
  )

  val densityFields = Seq[(String, Double, Double)](
    ("Food Sales-Grocery Store Workers on Main Shift Quantity", 0.4, 1.5),
    ("Food Sales-Grocery Store Refrigeration Walk-in Quantity", 0.4, 1.5)
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

