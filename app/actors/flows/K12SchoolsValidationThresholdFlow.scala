package actors.flows

import javax.inject.Inject

import actors.validators.bedes.{BedesDensityValidator, BedesDensityValidatorProps}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import com.maalka.bedes.BEDESTransformResult
import play.api.Configuration
import play.api.libs.json.Json

/**
  * Created by clayteeter on 7/14/17.
  */


class K12SchoolsValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                         override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  val bedesUse = Seq("K-12 School", "Education")

  val rangeFields = Seq(
    BedesValidationThreshold("Site Energy Resource Intensity", 56, 208),
    //BedesValidationThreshold("Education Gross Area", 23211, 284599))
    BedesValidationThreshold("Gross Floor Area", 23211, 284599)
  )


  val densityFields = Seq(
    BedesValidationThreshold("Education Computer Quantity", 0.7, 5.2),
    BedesValidationThreshold("Education Refrigeration Walk-in Quantity", 0.0, 0.04)
  )
}


