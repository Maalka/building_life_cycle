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


class HospitalValidationThresholdFlow @Inject ()( override implicit val actorSystem: ActorSystem,
                                                       override implicit val configuration: Configuration)
  extends ValidateThresholdFlow {

  override val bedesUse = Seq("Hospital (General Medical & Surgical)", "Health care-Inpatient hospital")

  val parentValidator = "Hospital (General Medical & Surgical)"

  override val rangeFields = Seq(
    BedesValidationThreshold("Source Energy Resource Intensity", 211, 211),
    BedesValidationThreshold("EPA Calculated Gross Floor Area", 54000, 1426994))

  override val densityFields = Seq(
    BedesValidationThreshold("Health Care-Inpatient Hospital Workers on Main Shift Quantity", 1.2, 4.0),
    BedesValidationThreshold("Health Care-Inpatient Hospital Staffed Beds Quantity", 0.2, 1.1),
    BedesValidationThreshold("Health Care-Inpatient Hospital Medical Equipment Quantity", 0, 0.016)
  )
}

