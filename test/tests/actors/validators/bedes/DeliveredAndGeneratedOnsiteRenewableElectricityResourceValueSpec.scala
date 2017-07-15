package tests.actors.validators.bedes

import java.util.UUID

import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes.DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, _}
import com.maalka.bedes.{BEDESTransformTable, BedesDefinition}
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationLike
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class DeliveredAndGeneratedOnsiteRenewableElectricityResourceValueSpec() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with SpecificationLike {
  sequential

  def after = system.terminate()

  "An empty BEDESTransformResult " should {
    "return invalid" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Electricity Use - Grid Purchase and Generated from Onsite Renewable Systems (kWh)", None, None, bedesDefinition, false)

      system.actorOf(DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }
  }

  "A BEDESTransformResult " should {
    "return valid if electricity use is in range" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Electricity Use - Grid Purchase and Generated from Onsite Renewable Systems (kWh)", Some(80), None, bedesDefinition, false)

      Console.println(transformResult)

      system.actorOf(DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      Console.println(validationResult)
      validationResult.valid mustEqual true
    }
    
    "return invalid if electricity use is -1" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Electricity Use - Grid Purchase and Generated from Onsite Renewable Systems (kWh)", Some(-1), None, bedesDefinition, false)

      system.actorOf(DeliveredAndGeneratedOnsiteRenewableElectricityResourceValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      Console.println(validationResult)
      validationResult.valid mustEqual false
    }
  }
}

