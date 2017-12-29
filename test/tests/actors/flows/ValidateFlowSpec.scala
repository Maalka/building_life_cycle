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

package tests.actors.flows
import java.util.UUID
import javax.inject.Inject

import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes.{BedesDensityValidatorProps, GrossFloorArea}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, _}
import com.maalka.bedes.{BEDESTransformResult, BEDESTransformTable, BedesDefinition}
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationLike
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json
import akka.stream.scaladsl._
import actors.flows.{ValidateFlow, WorshipCenterValidationThresholdFlow}
import akka.stream.ActorMaterializer
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.duration._
import scala.concurrent.Await


class ValidateFlowSpec () extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with SpecificationLike {
  sequential

  def after = system.terminate()


  implicit val materializer = ActorMaterializer()

  val appBuilder = new GuiceApplicationBuilder()
  val injector = appBuilder.injector()
  val validateFlow = injector.instanceOf[ValidateFlow]

  def generateBedesResults(): Seq[BEDESTransformResult] = {
    Seq(
      BEDESTransformTable.defaultTable.findBedesComposite(
        "Site EUI (kBtu/ft²)", Some(3232), None),

      BEDESTransformTable.defaultTable.findBedesComposite(
        "Primary Property Type - Self Selected", Some("Office"), None),

      BEDESTransformTable.defaultTable.findBedesComposite(
        "Property Floor Area (Buildings) (ft²)", Some(12000.0), Some("ft²"))
    )
  }


  "An Valid BEDESTransformResult set" should {
    "return valid valid GFA if set correctly" in {


      val transformResults = Seq(
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Site EUI (kBtu/ft²)", Some(3232), None),

        BEDESTransformTable.defaultTable.findBedesComposite(
          "Primary Property Type - Self Selected", Some("Office"), None),

        BEDESTransformTable.defaultTable.findBedesComposite(
          "Property Floor Area (Buildings) (ft²)", Some(12000.0), Some("ft²"))
      )


      val sinkUnderTest = Flow[Int].map(_ * 2).toMat(Sink.fold(0)(_ + _))(Keep.right)

      val future = Source.single(transformResults).via(validateFlow.run).runWith(Sink.seq)
      val result = Await.result(future, 20.seconds)

      // get gross floor area and check that it's true

      val flattenedResults = result.foldLeft(Seq.empty[Either[Throwable, UpdateObjectValidatedDocument]]) {
        case (l, r) => l ++ r.flatten
      }
      assert (flattenedResults.flatMap(_.right.toOption).exists{ d =>
        Console.println(d)
        d.validator == "Gross Floor Area" && d.valid
      })
      success
    }
  }


  "An Valid BEDESTransformResult set" should {
    "return invalid num computers density if set to 0" in {


      val transformResults = Seq(
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Office - Number of Computers", Some(0), None),

        BEDESTransformTable.defaultTable.findBedesComposite(
          "Primary Property Type - Self Selected", Some("Office"), None),

        BEDESTransformTable.defaultTable.findBedesComposite(
          "Property Floor Area (Buildings) (ft²)", Some(12000.0), Some("ft²"))
      )


      val sinkUnderTest = Flow[Int].map(_ * 2).toMat(Sink.fold(0)(_ + _))(Keep.right)

      val future = Source.single(transformResults).via(validateFlow.run).runWith(Sink.seq)
      val result = Await.result(future, 20.seconds)

      // get gross floor area and check that it's true

      val flattenedResults = result.foldLeft(Seq.empty[Either[Throwable, UpdateObjectValidatedDocument]]) {
        case (l, r) => l ++ r.flatten
      }
      assert (!flattenedResults.flatMap(_.right.toOption).exists{ d =>
        Console.println(d)
        d.validator == "Office Computer Quantity" && d.valid
      })
      success
    }
  }



  "An Valid BEDESTransformResult set" should {
    "return valid num computers density if set to 100 and gfa set to 100" in {


      val transformResults = Seq(
        BEDESTransformTable.defaultTable.findBedesComposite(
          "Office - Number of Computers", Some(100), None),

        BEDESTransformTable.defaultTable.findBedesComposite(
          "Primary Property Type - Self Selected", Some("Office"), None),

        BEDESTransformTable.defaultTable.findBedesComposite(
          "Property Floor Area (Buildings) (ft²)", Some(100.0), Some("ft²"))
      )


      val sinkUnderTest = Flow[Int].map(_ * 2).toMat(Sink.fold(0)(_ + _))(Keep.right)

      val future = Source.single(transformResults).via(validateFlow.run).runWith(Sink.seq)
      val result = Await.result(future, 20.seconds)

      // get gross floor area and check that it's true

      val flattenedResults = result.foldLeft(Seq.empty[Either[Throwable, UpdateObjectValidatedDocument]]) {
        case (l, r) => l ++ r.flatten
      }
      assert (flattenedResults.flatMap(_.right.toOption).exists{ d =>
        d.validator == "Office Computer Quantity" && d.valid
      })
      success
    }
  }


//
//  "An positive BEDESTransformResult" should {
//    "return valid if the value is 100" in {
//
//
//      lazy val appBuilder = new GuiceApplicationBuilder()
//      lazy val injector = appBuilder.injector()
//      lazy val validateFlow = injector.instanceOf[ValidateFlow]
//
//      implicit val materializer = ActorMaterializer()
//
//      def generateBedesResults(): Seq[BEDESTransformResult] = {
//        val bedesDefinition = BedesDefinition()
//
//        Seq(
//          BEDESTransformTable.defaultTable.findBedesComposite(
//            "Site EUI (kBtu/ft²)", Some("asdfasdf"), None, bedesDefinition, false),
//          BEDESTransformTable.defaultTable.findBedesComposite(
//            "Property Floor Area (Buildings) (ft²)", Some(12000.0), Some("ft²"), bedesDefinition)
//        )
//      }

//      val bedesDefinition = BedesDefinition()
//
//      val sinkUnderTest = Flow[Int].map(_ * 2).toMat(Sink.fold(0)(_ + _))(Keep.right)
//
//      val future = Source.single(generateBedesResults()).via(validateFlow.run).runWith(Sink.headOption)
//
//      val result = Await.result(future, 20.seconds)
//
//      Console.println(result)
//      assert(true)
//    }
//  }





}