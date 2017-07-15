package tests.actors.validators.bedes

import java.util.UUID

import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes.EnergyStarScoreAssessmentValue
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, _}
import com.maalka.bedes.{BEDESTransformTable, BedesDefinition}
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationLike
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class EnergyStarScoreAssessmentValueSpec() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with SpecificationLike {
  sequential

  def after = system.terminate()

  "An empty BEDESTransformResult " should {
    "return valid" in {
      val bedesDefinition = BedesDefinition()

      val transformResultESScore = BEDESTransformTable.defaultTable.findBedesComposite(
        "ENERGY STAR Score", None, None, bedesDefinition, false)

      val transformResultPT = BEDESTransformTable.defaultTable.findBedesComposite(
        "Primary Property Type - EPA Calculated", None, None, bedesDefinition, false)

      system.actorOf(EnergyStarScoreAssessmentValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(),
        Option(Seq(transformResultESScore, transformResultPT)))

      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual true
    }
  }

  "An valid BEDESTransformResult " should {
    "return valid if energy star score is in range and the use type is in use set" in {
      val bedesDefinition = BedesDefinition()

      val transformResultESScore = BEDESTransformTable.defaultTable.findBedesComposite(
        "ENERGY STAR Score", Option(88), None, bedesDefinition, false)

      val transformResultPT = BEDESTransformTable.defaultTable.findBedesComposite(
        "Primary Property Type - EPA Calculated", Option("Office"), None, bedesDefinition, false)

      system.actorOf(EnergyStarScoreAssessmentValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(),
        Option(Seq(transformResultESScore, transformResultPT)))

      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])

      Console.println(validationResult)

      validationResult.valid mustEqual true
    }

    "return valid if energy star score is in not in range and the use type is not use set" in {

      val bedesDefinition = BedesDefinition()

      val transformResultESScore = BEDESTransformTable.defaultTable.findBedesComposite(
        "ENERGY STAR Score", Option(200), None, bedesDefinition, false)

      val transformResultPT = BEDESTransformTable.defaultTable.findBedesComposite(
        "Primary Property Type - EPA Calculated", Option("Museum"), None, bedesDefinition, false)

      system.actorOf(EnergyStarScoreAssessmentValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(),
        Option(Seq(transformResultESScore, transformResultPT)))

      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])

      Console.println(validationResult)

      validationResult.valid mustEqual true

    }

    "return invalid if energy star score is in not in range and the use type is use set" in {

      val bedesDefinition = BedesDefinition()

      val transformResultESScore = BEDESTransformTable.defaultTable.findBedesComposite(
        "ENERGY STAR Score", Option(200), None, bedesDefinition, false)

      val transformResultPT = BEDESTransformTable.defaultTable.findBedesComposite(
        "Primary Property Type - EPA Calculated", Option("Office"), None, bedesDefinition, false)

      system.actorOf(EnergyStarScoreAssessmentValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(),
        Option(Seq(transformResultESScore, transformResultPT)))

      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])

      Console.println(validationResult)

      validationResult.valid mustEqual false

    }
  }
}
