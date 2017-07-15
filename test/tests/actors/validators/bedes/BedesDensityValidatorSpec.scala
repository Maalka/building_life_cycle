package tests.actors.validators.bedes

import java.util.UUID

import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes.BedesDensityValidatorProps
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, _}
import com.maalka.bedes.{BEDESTransformTable, BedesDefinition}
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationLike
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json


@RunWith(classOf[JUnitRunner])
class BedesDensityValidatorSpec() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with SpecificationLike {
  sequential

  def after = system.terminate()

  val arguments = Json.obj (
    "compositeName" -> "Site Energy Resource Intensity",
    "min" -> 0,
    "max" -> 200
  )

  "An empty BEDESTransformResult " should {
    "return invalid" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Property Floor Area (Buildings) (ft²)", None, Some("ft²"), bedesDefinition, false)

      system.actorOf(BedesDensityValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }
  }
  "An string BEDESTransformResult " should {
    "return invalid" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = Seq(
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Site EUI (kBtu/ft²)", Some("asdfasdf"), None, bedesDefinition, false),
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Property Floor Area (Buildings) (ft²)", Some(12000.0), Some("ft²"), bedesDefinition)
      )
      system.actorOf(BedesDensityValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(transformResult))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }
  }
  "An valid BEDESTransformResult " should {
    "return invalid if the density if negitive" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = Seq(
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Site EUI (kBtu/ft²)", Some(-1), None, bedesDefinition, false),
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Property Floor Area (Buildings) (ft²)", Some(12000.0), Some("ft²"), bedesDefinition)
      )

      system.actorOf(BedesDensityValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(transformResult))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }
    "return invalid if the density is 10000" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = Seq(
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Site EUI (kBtu/ft²)", Some(1), None, bedesDefinition, false),
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Property Floor Area (Buildings) (ft²)", Some(10000.0), Some("ft²"), bedesDefinition)
      )

      system.actorOf(BedesDensityValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(transformResult))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      Console.println(validationResult)
      validationResult.valid mustEqual false
    }
  }
  "An positive BEDESTransformResult " should {
    "return valid if the density is 1" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = Seq(
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Site EUI (kBtu/ft²)", Some(100), None, bedesDefinition, false),
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Property Floor Area (Buildings) (ft²)", Some(100.0), Some("ft²"), bedesDefinition)
      )

      system.actorOf(BedesDensityValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(transformResult))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      Console.println(validationResult)
      validationResult.valid mustEqual true
    }
  }
}