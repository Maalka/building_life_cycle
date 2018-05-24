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

package controllers

import java.io.{BufferedOutputStream, ByteArrayInputStream, ByteArrayOutputStream, File, FileOutputStream, InputStream, OutputStream, PrintWriter, StringWriter}
import java.util.Base64

import actors.flows._
import akka.actor.ActorSystem
import akka.stream._
import com.google.inject.Inject
import play.api.mvc._
import com.maalka.bedes.{BEDESTransform, BEDESTransformResult}
import akka.stream.scaladsl._
import akka.util.ByteString
import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}
import models.Measure
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.util.WorkbookUtil
import org.apache.poi.ss.usermodel.{Cell, Row, Sheet, Workbook}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import scala.collection.JavaConversions._
import org.joda.time.{DateTime, LocalDate}
import play.api.Logger

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.libs.json._

class BuildingLifeCycle @Inject()(
                             implicit actorSystem: ActorSystem,
                             validateFlow: ValidateFlow
                            ) extends Controller {


  implicit val BigIntWrite: Writes[BigInt] = new Writes[BigInt] {
    override def writes(bigInt: BigInt): JsValue = JsString(bigInt.toString())
  }

  implicit val BigIntRead: Reads[BigInt] = Reads {
    case JsString(value) => JsSuccess(scala.math.BigInt(value))
    case JsNumber(value) => JsSuccess(value.toBigInt())
    case _ => JsError(s"Invalid BigInt")
  }

  implicit val measureReads = Json.reads[Measure]
  implicit val measureWrites = Json.writes[Measure]

  def readme = Action {
    Ok.sendFile(
      content = new java.io.File("README.md"),
      inline = false)
  }

  def getFile = Action.async { request =>

    Future {
      val tt = "123"

      val data = Base64.getDecoder.decode(tt.getBytes());
      val is: InputStream = new ByteArrayInputStream(data);
      val CHUNK_SIZE = 100
      val dataContent = StreamConverters.fromInputStream(() => is, CHUNK_SIZE)
      Ok.chunked(dataContent).as("application/pdf").withHeaders(CONTENT_DISPOSITION -> "attachment; filename=aa.pdf")
    }

  }


  def buildXlsx = Action.async(parse.json) { request =>

    val systems = (request.body \ "systems").as[List[JsObject]]

    val systemTypes = systems.groupBy(_.fields.head._1)

    Logger.info("systemTypes: " + systemTypes)

    val workbook: XSSFWorkbook = new XSSFWorkbook


    // Measures Sheet
    val sheet1 = workbook.createSheet("Measures")

    val fieldNames = List("systemType", "detail", "implementationStatus", "startDate", "endDate", "comment", "buildingName", "buildingAddress")

    // header
    val row = sheet1.createRow(0)
    fieldNames.zipWithIndex.foreach {
      case (field, index) => {
        val cell = row.createCell(index)
        cell.setCellValue(field)
      }
    }

    val measures = (request.body \ "measures").toOption.map { m =>
      m.as[List[Measure]]
    }

    measures.map { m =>
      m.zipWithIndex.foreach {
        case (measure, index) => {
          val row = sheet1.createRow(index + 1)
          fieldNames.zipWithIndex.foreach {
            case (fieldName, fieldIndex) => {
              val cell = row.createCell(fieldIndex)
              cell.setCellValue(extract(measure, fieldName).getOrElse(""))
            }
          }
        }
      }
    }

    // Start Systems sheets

    val commonFields = List(
      "Manufacturer",
      "ModelNumber",
      "Quantity",
      "YearInstalled",
      "YearofManufacture"
    )

    val systemsHeaders = Map(
      "HVACSystem" -> (List(
        "HVACSystem",
        "HeatingAndCoolingSystems",
        "HeatingAndCoolingSystems:CoolingSource",
        "HeatingAndCoolingSystems:CoolingSource:CoolingSourceType",
        "HeatingAndCoolingSystems:CoolingSource:AnnualCoolingEfficiencyUnits",
        "HeatingAndCoolingSystems:CoolingSource:AnnualCoolingEfficiencyValue",
        "HeatingAndCoolingSystems:CoolingSource:Capacity",
        "HeatingAndCoolingSystems:CoolingSource:CapacityUnits",
        "HeatingAndCoolingSystems:CoolingSource:CoolingSourceType",
        "HeatingAndCoolingSystems:CoolingSource:CoolingSourceType:DX:DXSystemType",
        "HeatingAndCoolingSystems:CoolingSource:CoolingSourceType:EvaporativeCooler:EvaporativeCoolingType",
        "HeatingAndCoolingSystems:CoolingSource:CoolingSourceType:EvaporativeCooler:EvaporativeCoolingType",
        "HeatingAndCoolingSystems:CoolingSource:CoolingSourceType:NoCooling:NoCooling",
        "HeatingAndCoolingSystems:CoolingSource:CoolingSourceType:OtherCombination:OtherCombination",
        "HeatingAndCoolingSystems:CoolingSource:CoolingSourceType:Unknown:Unknown",
        "HeatingAndCoolingSystems:CoolingSource:CoolingSourceType:ZoningSystemType",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType",
        "HeatingAndCoolingSystems:HeatingSource:AnnualHeatingEfficiencyUnit",
        "HeatingAndCoolingSystems:HeatingSource:AnnualHeatingEfficiencyValue",
        "HeatingAndCoolingSystems:HeatingSource:CapacityUnits",
        "HeatingAndCoolingSystems:HeatingSource:HeatingMedium",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:Furnace:BurnerType",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:Furnace:CombustionEfficiency",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:Furnace:FurnaceType",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:Furnace:ThermalEfficiency",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:HeatPump:HeatPumpType",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:NoHeating:NoHeating",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:OtherCombination:OtherCombination",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:Unknown:Unknown",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:Unknown:Unknown",
        "HeatingAndCoolingSystems:HeatingSource:HeatingSourceType:ZoningSystemType",
        "OtherHVACSystems:Integration",
        "OtherHVACSystems:OtherHVACType",
        "OtherHVACSystems:OtherHVACType",
        "Plants",
        "Plants:CoolingPlantType",
        "Plants:CoolingPlantType:Chiller:AbsorptionHeatSource",
        "Plants:CoolingPlantType:Chiller:AnnualCoolingEfficiencyUnits",
        "Plants:CoolingPlantType:Chiller:AnnualCoolingEfficiencyValue",
        "Plants:CoolingPlantType:Chiller:Capacity",
        "Plants:CoolingPlantType:Chiller:CapacityUnits",
        "Plants:CoolingPlantType:Chiller:ChillerType",
        "Plants:CoolingPlantType:Chiller:Quantity",
        "Plants:CoolingPlantType:Chiller:ThirdPartyCertification",
        "Plants:CoolingPlantType:DistrictChilledWater:AnnualCoolingEfficiencyUnits",
        "Plants:CoolingPlantType:DistrictChilledWater:AnnualCoolingEfficiencyValue",
        "Plants:CoolingPlantType:DistrictChilledWater:Capacity",
        "Plants:CoolingPlantType:DistrictChilledWater:CapacityUnits",
        "Plants:CoolingPlantType:NoCooling:NoCooling",
        "Plants:CoolingPlantType:OtherCombination:OtherCombination",
        "Plants:CoolingPlantType:Unknown:Unknown",
        "Plants:HeatingPlantType",
        "Plants:HeatingPlantType:Boiler:BoilerType",
        "Plants:HeatingPlantType:Boiler:AnnualHeatingEfficiencyUnit",
        "Plants:HeatingPlantType:Boiler:AnnualHeatingEfficiencyValue",
        "Plants:HeatingPlantType:Boiler:BurnerType",
        "Plants:HeatingPlantType:Boiler:CapacityUnits",
        "Plants:HeatingPlantType:Boiler:CombustionEfficiency",
        "Plants:HeatingPlantType:Boiler:Quantity",
        "Plants:HeatingPlantType:Boiler:ThermalEfficiency",
        "Plants:HeatingPlantType:DistrictHeating:DistrictHeatingType",
        "Plants:HeatingPlantType:DistrictHeating:AnnualHeatingEfficiencyUnit",
        "Plants:HeatingPlantType:DistrictHeating:AnnualHeatingEfficiencyValue",
        "Plants:HeatingPlantType:DistrictHeating:Quantity",
        "Plants:HeatingPlantType:NoHeating:NoHeating",
        "Plants:HeatingPlantType:OtherCombination:OtherCombination",
        "Plants:HeatingPlantType:SolarThermal:AnnualHeatingEfficiencyUnit",
        "Plants:HeatingPlantType:SolarThermal:AnnualHeatingEfficiencyValue",
        "Plants:HeatingPlantType:SolarThermal:CapacityUnits",
        "Plants:HeatingPlantType:SolarThermal:OutputCapacity",
        "Plants:HeatingPlantType:SolarThermal:Quantity"
      ) ::: commonFields),

      "DomesticHotWaterSystem" -> (List(
        "DomesticHotWaterSystem",
        "HeatExchanger:HeatExchanger",
        "Instantaneous:InstantaneousWaterHeatingSource",
        "StorageTank:TankVolume",
        "HotWaterDistributionType",
        "WaterHeaterEfficiency",
        "WaterHeaterEfficiencyType",
        "Capacity",
        "CapacityUnits"
      ) ::: commonFields),

      "FanSystem" -> (List(
        "FanApplication",
        "FanControlType",
        "FanType"
      ) ::: commonFields),

      "FenestrationSystem" -> (List(
        "FenestrationType",
        "Window:WindowHeight",
        "Window:WindowWidth",
        "Window:WindowHorizontalSpacing",
        "Window:WindowOrientation",
        "Other:Other",
        "FenestrationFrameMaterial",
        "GlassType",
        "FenestrationGlassLayers",
        "FenestrationOperation",
        "Weatherstripped"
      ) ::: commonFields),

      "HeatRecoverySystem" -> (List(
        "HeatRecoveryType",
        "EnergyRecoveryEfficiency",
        "HeatRecoveryEfficiency"
      ) ::: commonFields),

      "LightingSystem" -> (List(
        "OutsideLighting",
        "LampType",
        "LampType:LampLabel",
        "LampType:Neon",
        "LampType:OtherCombination",
        "LampType:Unknown",
        "InstallationType",
        "LightingControlTypeOccupancy",
        "LightingDirection"
      ) ::: commonFields)
   )

    systemsHeaders.map { sh =>
      val safeName = WorkbookUtil.createSafeSheetName(sh._1)
      val sheet2 = workbook.createSheet(safeName)
      val headerRow = sheet2.createRow(0)

      sh._2.zipWithIndex.foreach {
        case (f, i) =>
          val cell = headerRow.createCell(i)
          cell.setCellValue(f.split(":").reverse.head)
      }

      systemTypes.filter( st => st._1.substring(4, st._1.length-1) == sh._1).zipWithIndex.foreach {
        case (systemFields, i: Int) =>
          systemFields._2.zipWithIndex.foreach {
            case (sf, fi) =>
              val dataRow = sheet2.createRow(fi+1)
              val excelField = buildPath(sf, List.empty[String])
              Logger.info("excelField: " + excelField)
              excelField.zipWithIndex.foreach {
                case (ef, ei) =>
                  Logger.info("searching in: " + sh._2)
                  Logger.info("searching for: " + ef._1(1))
                  val needle = if (ef._1(1).startsWith("auc:")) ef._1(1).substring(4) else ef._1(1)
                  Logger.info("searching for needle: " + needle)

                  val cellIndex = sh._2.indexWhere( fieldName => fieldName.split(":").reverse.head == needle)
                  if (cellIndex == -1) {
                    Logger.info("not found: " + ef._2)
                  }
                  // some fields are not mapped, just putting them on the sheet for inspection
                  val cell = dataRow.createCell(if (cellIndex == -1) cellIndex+50 else cellIndex)
                  ef._2 match {
                    case c: String => cell.setCellValue(c)
                    case c: Int => cell.setCellValue(c)
                    case c: Boolean => cell.setCellValue(c)
                  }
              }
          }
          }
      }

//    val ff = new File("workbook.xlsx")
//    BufferedOutputStream
//    val fileOut: OutputStream = new FileOutputStream(ff)

//    workbook.write(fileOut)
//
//    fileOut.flush()
//    fileOut.close()

    val bos = new ByteArrayOutputStream
    workbook.write(bos)

    val bytes: Array[Byte] = bos.toByteArray

    val is: InputStream = new ByteArrayInputStream(bytes);


    val CHUNK_SIZE = 100
    val dataContent = StreamConverters.fromInputStream(() => is, CHUNK_SIZE)

    Future {
      Ok.chunked(dataContent).withHeaders(
        "Content-Type" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
        "Content-Disposition" -> "attachment; filename=out.xlsx"
      )
    }
  }

  private def buildPath(in: JsValue, path: List[String]): Map[List[String], Any] = {
    in match {
      case i: JsObject => {
        i.value.flatMap { f =>
          buildPath(f._2, f._1 :: path):  Map[List[String], Any]
        }
      }.toMap
      case i: JsString => {
        Map(path -> i.as[String])
      }
      case i: JsNumber => {
        Map(path -> i.as[Int])
      }
      case i: JsBoolean => {
        Map(path -> i.as[Boolean])
      }
    }
  }

  private def extract(measure: Measure, fieldName: String): Option[String] = {
    fieldName match {
         case "systemType" => Some(measure.systemType)
         case "detail" => Some(measure.detail)
         case "implementationStatus" => Some(measure.implementationStatus)
         case "startDate" => Some(measure.startDate.toString())
         case "endDate" => Some(measure.endDate.toString())
         case "comment" => measure.comment
         case _ => None
    }
  }

  val measureFields = List("systemType", "detail", "implementationStatus", "startDate", "endDate", "comment", "buildingName", "buildingAddress")

  def parseXls = Action.async(parse.multipartFormData) { request =>

      val result: Option[Future[Result]] = for {
        uploadType <- request.body.dataParts.get("type")
        uploadFile <- request.body.file("inputData")
      } yield {
        uploadType.headOption match {
          case Some("measures") => {

            val pkg: OPCPackage = OPCPackage.open(uploadFile.ref.file)
            import org.apache.poi.xssf.usermodel.XSSFWorkbook
            val wb = new XSSFWorkbook(pkg)
            val measures = (0 until wb.getNumberOfSheets()).map { i =>
              wb.getSheetAt(i) }
            .filter(_.getSheetName.toLowerCase == "measures")
            .flatMap { sheet =>
                sheet.rowIterator().toSeq
              }
              // drop header
            .drop(1)
            .map { row =>
              Measure(row.getCell(0).toString,
                      row.getCell(1).toString,
                      row.getCell(2).toString,
                      BigInt(1),
                      BigInt(2),
                      Some(row.getCell(4).toString),
                      Some("building"),
                      Some("address"))
            }.toList

            Future { Ok(Json.toJson(measures))}
          }

          case Some("systems") => {
            val reader = CSVReader.open(uploadFile.ref.file)
//            Future { Ok(Json.toJson(readSystems(reader.all())))}
            Future { Ok(Json.toJson(""))}
          }
        }
      }

    result.getOrElse {
      Future {Ok("failed")}
    }

  }

  private def readMeasures(readings: List[List[String]]): List[Measure] = {
    readings.map {
      case List(systemType, detail, implementationStatus, startDate, endDate, comments) =>
        Measure(systemType, detail, implementationStatus, BigInt(startDate), BigInt(endDate), Some(comments), None, None)
    }
  }

//  private def readSystems(readings: List[List[String]]): List[models.System] = {
//    readings.map {
//      r => models.System(r)
//    }
//  }

  private def parseDate(date: String): DateTime = {
    import org.joda.time.format.DateTimeFormat
    import org.joda.time.format.DateTimeFormatter
    val formatter = DateTimeFormat.forPattern("MM/dd/yyyy")
    formatter.parseDateTime(date)
  }

  def validate = Action.async(parse.multipartFormData) { request =>

    implicit val timeout = akka.util.Timeout(5 seconds)

    val decider: Supervision.Decider = {
      case NonFatal(th) =>

        val sw = new StringWriter
        th.printStackTrace(new PrintWriter(sw))
        // print this to stdout as well
        // TODO: Fix loging
        Console.println(sw.toString)
        Supervision.Resume

      case _ => Supervision.Stop
    }

    implicit val materializer = ActorMaterializer(
      ActorMaterializerSettings(actorSystem).withSupervisionStrategy(decider)
    )

    request.body.file("inputData").map { file =>
      Source.fromIterator(() => BEDESTransform.fromXLS(None, file.ref.file, None, None))
        .groupBy(10000, _._1.propertyId.get)
          .log("Validating Property")
        .fold(Seq.empty[BEDESTransformResult])(_ :+ _._1)
        .mergeSubstreams
        .via(validateFlow.run)
        .map(_.map(_.map(_.right.get)))
        .runWith(Sink.seq) map { res =>
        Ok(Json.obj(
          "result" -> Json.toJson(res.map(Json.toJson(_))),
          "status" -> JsString("OK")
        ))
      }
    }.getOrElse {
      Future(Ok("Failure"))
    }
  }
}

