package actors.flows

import javax.inject.Inject

import actors.validators.bedes.BedesDensityValidatorProps
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import com.maalka.bedes.BEDESTransformResult
import play.api.Configuration
import play.api.libs.json.Json

/**
  * Created by clayteeter on 7/14/17.
  */


class FinancialOfficeValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                              override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  override val bedesUse = Seq("Financial Office", "Office")

  override val rangeFields = Seq(
    BedesValidationThreshold("Site Energy Resource Intensity", 98, 525),
    //BedesValidationThreshold("Office Gross Area", 10000, 1034547),
    BedesValidationThreshold("Gross Floor Area", 10000, 1034547),

    BedesValidationThreshold("Office Business Average Weekly Hours", 44, 130)
  )

  override val densityFields = Seq(
    BedesValidationThreshold("Office Workers on Main Shift Quantity", 0.4, 3.4),
    BedesValidationThreshold("Office Computer Quantity", 0.7, 7.9)
  )
}


