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


case class RefrigeratedWarehouseValidationFlow @Inject()(validateFlow: ValidateFlow) {
  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 17, 274),
    ("Warehouse-Refrigerated Gross Area", 9992, 887014),
    ("Warehouse-Refrigerated Business Average Weekly Hours", 40, 168)
  )

  val densityFields = Seq[(String, Double, Double)](
    ("Warehouse-Refrigerated Workers on Main Shift Quantity", 0.1, 1.5)
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}

