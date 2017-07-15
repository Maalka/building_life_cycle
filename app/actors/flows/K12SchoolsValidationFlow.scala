package actors.flows

import javax.inject.Inject

import actors.validators.bedes.{BedesDensityValidator, BedesDensityValidatorProps}
import akka.NotUsed
import akka.stream.scaladsl._
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.Json

/**
  * Created by clayteeter on 7/14/17.
  */


case class K12SchoolsValidationFlow @Inject()(validateFlow: ValidateFlow) {

  val fields = Seq[(String, Double, Double)](
    ("Site Energy Resource Intensity", 56, 208),
    ("Education Gross Area", 23211, 284599))

  val densityFields = Seq[(String, Double, Double)](
    ("Education Computer Quantity", 0.7, 5.2),
    ("Education Refrigeration Walk-in Quantity", 0.0, 0.04)
  )

  def run = {
    Flow[Seq[BEDESTransformResult]].via(validateFlow.run())
  }
}


