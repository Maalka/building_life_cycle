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


/**
  * Created by clayteeter on 7/17/17.
  */
class  ValidateFlow @Inject () (implicit actorSystem: ActorSystem,
                                bankValidationFlow: BankValidationThresholdFlow,
                                courhouseValidationFlow: CourthouseValidationThresholdFlow,
                                financialOfficeValiatinFlow: FinancialOfficeValidationThresholdFlow,
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
      CompletedConstructionStatusDate,
      CustomEnergyMeteredPremisesLabel,
      DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue,
      ObservedPrimaryOccupancyClassification,
      PortfolioManagerPropertyIdentifier,
      PremisesAddressLine1,
      PremisesCity,
      PremisesState,
      SiteEnergyResourceIntensity,
      WeatherNormalizedSourceEnergyResourceIntensity
    )

    val thresholdValidators = Seq(
      bankValidationFlow,
      courhouseValidationFlow,
      financialOfficeValiatinFlow,
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
    val o = builder.add(Merge[Seq[Seq[Either[Throwable, UpdateObjectValidatedDocument]]]](2))

    in.out(0) ~> thresholdFanOut
    in.out(1) ~> validatorsFanOut

    thresholdZip ~> o
    validatorsZip ~> o
    FlowShape(in.in, o.out)

  })
}
