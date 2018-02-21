/*
 * Copyright 2017 Maalka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package actors.flows

import java.util.UUID
import javax.inject.Inject

import actors.CommonMessage
import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes._
import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl._
import akka.pattern.ask

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import com.maalka.bedes.BEDESTransformResult

class  ValidateFlow @Inject () (implicit actorSystem: ActorSystem,
                                bankValidationFlow: BankValidationThresholdFlow,
                                courhouseValidationFlow: CourthouseValidationThresholdFlow,
                                financialOfficeValiationFlow: FinancialOfficeValidationThresholdFlow,
                                hospitalValidationFlow: HospitalValidationThresholdFlow,
                                hotelValidationFlow: HotelValidationThresholdFlow,
                                k12SchoolsValidationFlow: K12SchoolsValidationThresholdFlow,
                                medicalOfficeValidationFlow: MedicalOfficeValidationThresholdFlow,
                                nonRefrigeratedWarehouseValidationFlow: NonRefrigeratedWarehouseValidationThresholdFlow,
                                officeValidationFlow: OfficeValidationThresholdFlow,
                                refrigeratedWarehouseValidationFlow: RefrigeratedWarehouseValidationThresholdFlow,
                                residenceHallValidationFlow: ResidenceHallValidationThresholdFlow,
                                retailValidationFlow: RetailValidationThresholdFlow,
                                seniorCareValidationFlow: SeniorCareValidationThresholdFlow,
                                supermarketValidationFlow: SupermarketValidationThresholdFlow,
                                worshipCenterValidationFlow: WorshipCenterValidationThresholdFlow
                               )  {


  def run = Flow.fromGraph(GraphDSL.create() { implicit builder =>
    // validators
    import GraphDSL.Implicits._

    // PremisesNameIdentifier must be the first response
    val validators = Seq(
      PremisesNameIdentifier,
      PremisesAddressLine1,
      PremisesCity,
      //PremisesState,
      PremisesZIPCode,
      GrossFloorArea,

      CompletedConstructionStatusDate,

      CustomEnergyMeteredPremisesLabel,

      DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue,

      ObservedPrimaryOccupancyClassification,

      PortfolioManagerPropertyIdentifier,
      WeatherNormalizedSourceEnergyResourceIntensity,

      SiteEnergyResourceIntensity,
      EnergyStarScoreAssessmentValue

    )

    val thresholdValidators = Seq(
      bankValidationFlow,
      courhouseValidationFlow,
      //financialOfficeValiationFlow,
      hospitalValidationFlow,
      hotelValidationFlow,
      k12SchoolsValidationFlow,
      medicalOfficeValidationFlow,
      nonRefrigeratedWarehouseValidationFlow,
      officeValidationFlow,
      refrigeratedWarehouseValidationFlow,
      residenceHallValidationFlow,
      retailValidationFlow,
      seniorCareValidationFlow,
      supermarketValidationFlow,
      worshipCenterValidationFlow
    )


    val validatorsFanOut = builder.add(Broadcast[Seq[BEDESTransformResult]](validators.size))
    val validatorsZip = builder.add(ZipN[Seq[Either[Throwable, UpdateObjectValidatedDocument]]](validators.size))

    implicit val timeout = akka.util.Timeout(5 seconds)


    validators.zipWithIndex.foreach { case (v, i) =>
      validatorsFanOut.out(i) ~> Flow[Seq[BEDESTransformResult]].mapAsync(1) { r =>
        actorSystem.actorOf(v.props("", "", "", None, None)) ? Validator.Value(UUID.randomUUID(), Option(r)) map {
          case CommonMessage.Failed(refId, message, cause) => Seq(Left(cause))
          case d: UpdateObjectValidatedDocument => Seq(Right(d))
        }
      } ~> validatorsZip.in(i)
    }

    val thresholdFanOut = builder.add(Broadcast[Seq[BEDESTransformResult]](thresholdValidators.size))
    val thresholdZip = builder.add(ZipN[Seq[Either[Throwable, UpdateObjectValidatedDocument]]](thresholdValidators.size))

    thresholdValidators.zipWithIndex.foreach { case (v, i ) =>
      thresholdFanOut.out(i).via(v.run()) ~> thresholdZip.in(i)
    }


    val in = builder.add(Broadcast[Seq[BEDESTransformResult]](2))

    val o = builder.add(ZipWith[Seq[Seq[Either[Throwable, UpdateObjectValidatedDocument]]],
      Seq[Seq[Either[Throwable, UpdateObjectValidatedDocument]]],
      Seq[Seq[Either[Throwable, UpdateObjectValidatedDocument]]]] {
      case (a, b) => a ++ b
    })

    in.out(0) ~> thresholdFanOut
    in.out(1) ~> validatorsFanOut


    validatorsZip ~> o.in0
    thresholdZip ~> o.in1

    FlowShape(in.in, o.out)

  })
}
