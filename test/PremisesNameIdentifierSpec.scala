import java.util.UUID

import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes.PremisesNameIdentifier
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, _}
import org.junit.runner.RunWith
import org.scalatest._
import org.specs2.mutable.SpecificationLike
import org.specs2.specification._
import org.specs2.runner.JUnitRunner
import com.maalka.bedes.{BEDESRow, BEDESTransformResult, BEDESTransformTable, BedesDefinition}
import org.apache.poi.ss.formula.functions.Rows

import scala.concurrent.duration._


@RunWith(classOf[JUnitRunner])
class PremisesNameIdentifierSpec() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with SpecificationLike {
  sequential

  def after = system.terminate()

  "An empty BEDESTransformResult " should {
    "return invalid" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Property Name", None, None, bedesDefinition, false)

      system.actorOf(PremisesNameIdentifier.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }
  }
  "An string BEDESTransformResult " should {
    "return valid" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Property Name", Some("Building Name"), None, bedesDefinition, false)

      system.actorOf(PremisesNameIdentifier.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual true
    }
  }
}