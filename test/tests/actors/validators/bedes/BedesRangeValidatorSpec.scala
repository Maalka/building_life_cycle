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
import actors.validators.bedes.BedesRangeValidatorProps
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, _}
import com.maalka.bedes.{BEDESTransformTable, BedesDefinition}
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationLike
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json


@RunWith(classOf[JUnitRunner])
class BedesRangeValidatorSpec() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
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
      val transformResult = BEDESTransformTable.defaultTable.findBedesComposite(
        "Site EUI (kBtu/ft²)", None, Some("kWh"))

      system.actorOf(BedesRangeValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(Seq(transformResult)))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }
  }

  "An string BEDESTransformResult " should {
    "return invalid" in {
      val transformResult = Seq(BEDESTransformTable.defaultTable.findBedesComposite(
          "Site EUI (kBtu/ft²)", Some("asdfasdf"), None))

      system.actorOf(BedesRangeValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(transformResult))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }
  }

  "An valid BEDESTransformResult " should {
    "return invalid if the value if negitive" in {
      val transformResult = Seq(BEDESTransformTable.defaultTable.findBedesComposite(
          "Site EUI (kBtu/ft²)", Some(-1), None))

      system.actorOf(BedesRangeValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(transformResult))

      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      validationResult.valid mustEqual false
    }

    "return invalid if the value is 10000" in {
      val transformResult = Seq(BEDESTransformTable.defaultTable.findBedesComposite(
          "Site EUI (kBtu/ft²)", Some(10000), None))

      system.actorOf(BedesRangeValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(transformResult))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      Console.println(validationResult)
      validationResult.valid mustEqual false
    }
  }
  "An positive BEDESTransformResult " should {
    "return valid if the value is 100" in {
      val transformResult = Seq(BEDESTransformTable.defaultTable.findBedesComposite(
          "Site EUI (kBtu/ft²)", Some(100), None))

      system.actorOf(BedesRangeValidatorProps(Some(arguments)).props("", "", "", None)) ! Validator.Value(UUID.randomUUID(), Option(transformResult))
      val validationResult = expectMsgClass(classOf[UpdateObjectValidatedDocument])
      Console.println(validationResult)
      validationResult.valid mustEqual true
    }
  }
}