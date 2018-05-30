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

import actors.flows._
import akka.actor.ActorSystem
import akka.stream._
import com.google.inject.Inject
import play.api.mvc._
import com.maalka.bedes.{BEDESTransform, BEDESTransformResult}
import akka.stream.scaladsl._
import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}
import org.apache.poi.ss.usermodel._
import models.{Measure, MeasuresWithToken}
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.util.WorkbookUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import scala.collection.JavaConversions._
import org.joda.time.LocalDate
import org.joda.time.format.{DateTimeFormat, DateTimeFormatterBuilder, DateTimeParser}
import play.api.{Configuration, Logger}
import play.api.cache.CacheApi
import play.api.data.validation.ValidationError
import play.api.libs.Files.TemporaryFile

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.libs.json._

import scala.util.{Failure, Random, Try}

class BuildingLifeCycle @Inject()(
                             implicit actorSystem: ActorSystem,
                             validateFlow: ValidateFlow,
                             cache: CacheApi,
                             config: Configuration
                            ) extends Controller {

  implicit val localDateReads: Reads[LocalDate] = new Reads[LocalDate] {
    override def reads(json: JsValue): JsResult[LocalDate] = json match {
      case JsNumber(value) => {
        JsSuccess(new LocalDate(value.toLong))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.datetime"))))
    }
  }

  implicit val measureReads = Json.reads[Measure]
  implicit val measureWrites = Json.writes[Measure]

  implicit val measureWithTokenReads = Json.reads[MeasuresWithToken]
  implicit val measureWithTokenWrites = Json.writes[MeasuresWithToken]

  def getFile(token: Option[String]) = Action.async { request =>

    Logger.info("searching file by token: " + token.getOrElse(""))
    val file: Option[TemporaryFile] = token.flatMap { t =>
      cache.get[TemporaryFile](t)
    }

    file.map { tf =>
      Future {
        Ok.sendFile(
          content = tf.file,
          inline = false, onClose = () => { tf.clean })
      }
    }.getOrElse(
      Future {
        Ok("no file")
      }
    )
  }

  def buildXlsx = Action.async(parse.json) { request =>

    val bName = (request.body \ "building" \ "buildingName").asOpt[String]
    val bAddress = (request.body \ "building" \ "addressStreet").asOpt[String]

    val systems = (request.body \ "systems").as[List[JsObject]]

    val systemTypes = systems.groupBy(_.fields.head._1)

    val workbook: XSSFWorkbook = new XSSFWorkbook


    // Measures Sheet
    val sheet1 = workbook.createSheet("Measures")

    val measureFieldNames = List("buildingName", "buildingAddress", "systemType", "detail", "implementationStatus", "startDate", "endDate", "comment")

    // header
    val row = sheet1.createRow(0)
    measureFieldNames.zipWithIndex.foreach {
      case (field, index) => {
        val cell = row.createCell(index)
        cell.setCellValue(field)
      }
    }

    val measures = (request.body \ "measures").toOption.map { m =>
      m.as[List[Measure]]
    }

    val x = measures.map { m => m.map { i =>
      i.copy(buildingName = bName, buildingAddress = bAddress)
      }
    }

    x.map { m =>
      m.zipWithIndex.foreach {
        case (measure, index) => {
          val row = sheet1.createRow(index + 1)
          measureFieldNames.zipWithIndex.foreach {
            case (fieldName, fieldIndex) => {
              val cell = row.createCell(fieldIndex)
              cell.setCellValue(extract(measure, fieldName).getOrElse(""))
            }
          }
        }
      }
    }

    // Start Systems sheets

    val buildingFields = List(
      "buildingName",
      "buildingAddress"
    )

    val commonFields = List(
      "Manufacturer",
      "ModelNumber",
      "Quantity",
      "YearInstalled",
      "YearofManufacture"
    )

    val systemsHeaders = Map(
      "HVACSystem" -> (buildingFields ::: (List(
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
      ) ::: commonFields)),

      "DomesticHotWaterSystem" -> (buildingFields ::: (List(
        "DomesticHotWaterSystem",
        "HeatExchanger:HeatExchanger",
        "Instantaneous:InstantaneousWaterHeatingSource",
        "StorageTank:TankVolume",
        "HotWaterDistributionType",
        "WaterHeaterEfficiency",
        "WaterHeaterEfficiencyType",
        "Capacity",
        "CapacityUnits"
      ) ::: commonFields)),

      "FanSystem" -> (buildingFields ::: (List(
        "FanApplication",
        "FanControlType",
        "FanType"
      ) ::: commonFields)),

      "FenestrationSystem" -> (buildingFields ::: (List(
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
      ) ::: commonFields)),

      "HeatRecoverySystem" -> (buildingFields ::: (List(
        "HeatRecoveryType",
        "EnergyRecoveryEfficiency",
        "HeatRecoveryEfficiency"
      ) ::: commonFields)),

      "LightingSystem" -> (buildingFields ::: (List(
        "OutsideLighting",
        "LampType",
        "LampType:LampLabel",
        "LampType:Neon",
        "LampType:OtherCombination",
        "LampType:Unknown",
        "InstallationType",
        "LightingControlTypeOccupancy",
        "LightingDirection"
      ) ::: commonFields))
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
              val excelField: Map[List[String], Any] = buildPath(sf, List.empty[String])
              val buildingFields = Map(List("", "buildingName") -> bName.getOrElse(""), List("", "buildingAddress") -> bAddress.getOrElse(""))
              (buildingFields ++ excelField).zipWithIndex.foreach {
                case (ef, ei) =>
                  val needle = if (ef._1(1).startsWith("auc:")) ef._1(1).substring(4) else ef._1(1)
                  val cellIndex = sh._2.indexWhere( fieldName => fieldName.split(":").reverse.head == needle)
                  if (cellIndex == -1) {
                    Logger.error("not found field: " + ef._2)
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

    val bos = new ByteArrayOutputStream
    workbook.write(bos)

    import java.io.FileOutputStream
    val file = new File("/tmp/" + "lifecycle.xlsx")
    val tfile = TemporaryFile(file)
    val fos = new FileOutputStream(file)
    bos.writeTo(fos)
    fos.flush()
    fos.close()

    val token = Random.alphanumeric.take(10).toList.mkString("")

    cache.set(token, tfile)

    Future {
      Ok(token)
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
         case "buildingName" => measure.buildingName
         case "buildingAddress" => measure.buildingAddress
         case "systemType" => Some(measure.systemType)
         case "detail" => Some(measure.detail)
         case "implementationStatus" => Some(measure.implementationStatus)
         case "startDate" => Some(measure.startDate.toString(("MM/dd/yyyy")))
         case "endDate" => Some(measure.endDate.toString(("MM/dd/yyyy")))
         case "comment" => measure.comment
         case _ => None
    }
  }

  private def getCellValue(cell: Cell) = {
    import org.apache.poi.ss.usermodel.DataFormatter
    val formatter = new DataFormatter
    formatter.formatCellValue(cell)
  }

  def parseXls = Action.async(parse.multipartFormData) { request =>

    import org.joda.time.format.DateTimeFormat
    import org.joda.time.format.DateTimeParser
    val formatter =  new DateTimeFormatterBuilder().append(
      null,
      Array[DateTimeParser](
        DateTimeFormat.forPattern("MM/dd/yyyy").getParser,
        DateTimeFormat.forPattern("dd/MM/yyyy").getParser,
        DateTimeFormat.forPattern("dd-MM-yyyy").getParser,
        DateTimeFormat.forPattern("yyyy-MM-dd").getParser)
    ).toFormatter

      val result = for {
        uploadFile <- request.body.file("inputData")
      } yield {
          val pkg = OPCPackage.open(uploadFile.ref.file)
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
            Try {
              Measure(
                Some(getCellValue(row.getCell(0))),
                Some(getCellValue(row.getCell(1))),
                getCellValue(row.getCell(2)),
                getCellValue(row.getCell(3)),
                getCellValue(row.getCell(4)),
                formatter.parseLocalDate(getCellValue(row.getCell(5))),
                formatter.parseLocalDate(getCellValue(row.getCell(6))),
                Some(getCellValue(row.getCell(7)))
              )
            }
          }.toList

          // put errors xls into cache for later, return successes
          val workbook = new XSSFWorkbook
          val sheet1 = workbook.createSheet("Errors in measures")

          val failures = measures.filter(_.isFailure)
          failures.map { fails =>
            fails match {
              case Failure(e) => e
              case _ => new Exception("will not happen")
            }
          }.zipWithIndex.foreach {
            case (f, i) => {
              val row = sheet1.createRow(i + 1)
              val errorCell = row.createCell(0)
              errorCell.setCellValue(f.getMessage)
            }
          }

        val bos = new ByteArrayOutputStream
        workbook.write(bos)

        val file = new File("/tmp/" + "errors.xlsx")
        val tfile = TemporaryFile(file)
        val fos = new FileOutputStream(file)
        bos.writeTo(fos)
        fos.flush()
        fos.close()

        val token = Random.alphanumeric.take(10).toList.mkString("")

        Logger.info("creating error xls for token: " + token)

        cache.set(token, tfile)

        Future { Ok(Json.toJson(MeasuresWithToken(if (failures.length > 0) Some(token) else None, measures.filter(_.isSuccess).map{_.get} ))) }
        }

    result.getOrElse {
      Future {Ok("failed")}
    }

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

