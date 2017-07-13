import java.util.UUID

import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes.PremisesCountry
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, _}
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationLike
import org.specs2.runner.JUnitRunner
import com.maalka.bedes.{BEDESTransformTable, BedesDefinition}



@RunWith(classOf[JUnitRunner])
class PremisesCountrySpec() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with SpecificationLike {
  sequential

  def after = system.terminate()

  "An empty BEDESTransformResult " should {
    "return invalid" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Country", None, None, bedesDefinition)

      system.actorOf(PremisesCountry.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }
  }
  "An string BEDESTransformResult " should {
    "return valid" in {
      val bedesDefinition = BedesDefinition()

      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Country", Some("Country"), None, bedesDefinition)

      system.actorOf(PremisesCountry.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual true
    }
  }
}
