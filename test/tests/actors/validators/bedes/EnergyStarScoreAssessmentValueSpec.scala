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
      val transformResultESScore = BEDESTransformTable.defaultTable.findBedesComposite(
        "ENERGY STAR Score", None, None)

      val transformResultPT = BEDESTransformTable.defaultTable.findBedesComposite(
        "Primary Property Type - EPA Calculated", None, None)

      system.actorOf(EnergyStarScoreAssessmentValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(),
        Option(Seq(transformResultESScore, transformResultPT)))

      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual true
    }
  }

  "An valid BEDESTransformResult " should {
    "return valid if energy star score is in range and the use type is in use set" in {
      val transformResultESScore = BEDESTransformTable.defaultTable.findBedesComposite(
        "ENERGY STAR Score", Option(88), None)

      val transformResultPT = BEDESTransformTable.defaultTable.findBedesComposite(
        "Primary Property Type - EPA Calculated", Option("Office"), None)

      system.actorOf(EnergyStarScoreAssessmentValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(),
        Option(Seq(transformResultESScore, transformResultPT)))

      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])

      Console.println(validationResult)

      validationResult.valid mustEqual true
    }

    "return valid if energy star score is in not in range and the use type is not use set" in {

      val transformResultESScore = BEDESTransformTable.defaultTable.findBedesComposite(
        "ENERGY STAR Score", Option(200), None)

      val transformResultPT = BEDESTransformTable.defaultTable.findBedesComposite(
        "Primary Property Type - EPA Calculated", Option("Museum"), None)

      system.actorOf(EnergyStarScoreAssessmentValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(),
        Option(Seq(transformResultESScore, transformResultPT)))

      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])

      Console.println(validationResult)

      validationResult.valid mustEqual true

    }

    "return invalid if energy star score is in not in range and the use type is use set" in {

      val transformResultESScore = BEDESTransformTable.defaultTable.findBedesComposite(
        "ENERGY STAR Score", Option(200), None)

      val transformResultPT = BEDESTransformTable.defaultTable.findBedesComposite(
        "Primary Property Type - EPA Calculated", Option("Office"), None)

      system.actorOf(EnergyStarScoreAssessmentValue.props("", "", "", None, None)) ! Validator.Value(UUID.randomUUID(),
        Option(Seq(transformResultESScore, transformResultPT)))

      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])

      Console.println(validationResult)

      validationResult.valid mustEqual false

    }
  }
}
