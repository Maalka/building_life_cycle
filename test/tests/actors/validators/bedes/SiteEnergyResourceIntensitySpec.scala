package tests.actors.validators.bedes

import java.util.UUID

import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes.SiteEnergyResourceIntensity
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, _}
import com.maalka.bedes.{BEDESTransformTable, BedesDefinition}
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationLike
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class SiteEnergyResourceIntensitySpec() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with SpecificationLike {
  sequential

  def after = system.terminate()

  "An empty BEDESTransformResult " should {
    "return invalid" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Site EUI (kBtu/ft²)", None, None, bedesDefinition, false)

      system.actorOf(SiteEnergyResourceIntensity.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }
  }
  "A BEDESTransformResult " should {
    "return valid if eui is in range" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Site EUI (kBtu/ft²)", Some(80), None, bedesDefinition, false)

      system.actorOf(SiteEnergyResourceIntensity.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      Console.println(validationResult)
      validationResult.valid mustEqual true
    }
    "return invalid if eui is -1" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Site EUI (kBtu/ft²)", Some(-1), None, bedesDefinition, false)

      system.actorOf(SiteEnergyResourceIntensity.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      Console.println(validationResult)
      validationResult.valid mustEqual false
    }
    "return invalid if eui is 376" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Site EUI (kBtu/ft²)", Some(376), None, bedesDefinition, false)

      system.actorOf(SiteEnergyResourceIntensity.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      Console.println(validationResult)
      validationResult.valid mustEqual false
    }
  }
}

