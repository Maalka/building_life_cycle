package actors.flows

import java.util.UUID

import actors.CommonMessage
import actors.ValidatorActors.BedesValidators.{BedesValidator, BedesValidatorCompanion}
import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes._
import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.pattern.ask
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, ZipN}
import javax.inject._

import akka.util.Timeout
import com.maalka.bedes.BEDESTransformResult
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by clayteeter on 7/14/17.
  */

trait ValidatableFlow {
  val fields: Seq[(String, Double, Double)]
  val densityFields: Seq[(String, Double, Double)]

  val validators = fields.map { f =>
    BedesDensityValidatorProps(Option(Json.obj("compositeField" -> f._1, "min" -> f._2, "max" -> f._3)))
  } ++ densityFields.map { f =>
    BedesDensityValidatorProps(Option(Json.obj("compositeField" -> f._1, "min" -> f._2, "max" -> f._3)))
  }
}

@Singleton
class ValidateFlow @Inject()(implicit val actorSystem: ActorSystem) extends ValidatableFlow {
  val fields = Seq.empty[(String, Double, Double)]
  val densityFields = Seq.empty[(String, Double, Double)]


  def run() = Flow.fromGraph(GraphDSL.create() { implicit builder =>
    // validators
    import GraphDSL.Implicits._

    // PremisesNameIdentifier must be the first response
    /*val validators = Seq(
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
    */
    implicit val timeout = Timeout(5 seconds)
    val fanOut = builder.add(Broadcast[Seq[BEDESTransformResult]](validators.size))
    val zip = builder.add(ZipN[Either[Throwable, UpdateObjectValidatedDocument]](validators.size))
    validators.zipWithIndex.foreach { case (v, i) =>
      fanOut.out(i) ~> Flow[Seq[BEDESTransformResult]].mapAsync(1) { r =>
        actorSystem.actorOf(v.props("", "", "", None, None)) ? Validator.Value(UUID.randomUUID(), Option(r)) map {
          case CommonMessage.Failed(refId, message, cause) => Left(cause)
          case d: UpdateObjectValidatedDocument => Right(d)
        }
      } ~> zip.in(i)
    }
    FlowShape(fanOut.in, zip.out)
  })

}
