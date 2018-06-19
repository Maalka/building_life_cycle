import org.scalatestplus.play.PlaySpec
import play.api.Logger
import play.api.libs.json.{JsObject, JsString, JsValue, Json}

class Parsing extends PlaySpec {


  "AAA" must {
    "test" in {
      val json =
        """
          |{"measures":[{"systemType":"Building Automation Systems","detail":"Add or upgrade BAS/EMS/EMCS","implementationStatus":"Evaluated","startDate":1525608000000,"endDate":1526126400000}],"systems":[{"auc:FanSystems":{"auc:FanSystem":{"auc:FanApplication":{"$":"Exhaust"},"auc:FanControlType":{"$":"Constant Volume"},"auc:FanType":{"$":"Axial"}}}},{"auc:FanSystems":{"auc:FanSystem":{"auc:FanApplication":{"$":"Supply"},"auc:FanControlType":{"$":"Stepped"},"auc:FanType":{"$":"Centrifugal"}}}},{"auc:HeatRecoverySystems":{"auc:HeatRecoverySystem":{"auc:HeatRecoveryType":{"$":"Earth to water heat exchanger"},"auc:EnergyRecoveryEfficiency":{"$":33}}}}]}
        """.stripMargin

        val systems = (Json.parse(json) \ "systems").as[List[JsObject]]

      Console.println(systems)

      val res = buildPath(systems.head, List.empty[String])
      Console.println("res: " + res)


      val o1 = Map(List("a", "b", "c") -> "pirmas")
      val o2 = o1 + Map(List("b", "a", "c") -> "antras")

    }
  }

  def buildPath(in: JsValue, path: List[String]): Map[List[String], String] = {
    in match {
      case i: JsObject => {
        i.value.flatMap { f =>
          buildPath(f._2, f._1 :: path):  Map[List[String], String]
        }
      }.toMap
      case i: JsString => {
        Map(path -> i.as[String])
      }
    }
  }

}
