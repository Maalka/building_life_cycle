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


class MedicalOfficeValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                            override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  val bedesUse = Seq("Medical Office", "Health care-Outpatient non-diagnostic")

  val parentValidator = "Medical Office"

  val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 84, 570),
    //BedesValidationThreshold("Health Care-Outpatient Non-diagnostic Gross Area", 7114, 202354))
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 7114, 202354)
  )

  val densityFields = Seq(
    BedesValidationThreshold("Health Care-Outpatient Non-diagnostic Business Average Weekly Hours", 40, 112),
    BedesValidationThreshold("Health Care-Outpatient Non-diagnostic Workers on Main Shift Quantity", 0.8, 5)
  )
}

