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

import java.io.{File, FileOutputStream, InputStream, OutputStream, PrintWriter, StringWriter}
import java.nio.file.Files
import java.util.UUID

import actors.CommonMessage
import actors.flows.BankValidationThresholdFlow
import actors.flows._
import actors.validators.Validator
import actors.validators.Validator.UpdateObjectValidatedDocument
import actors.validators.bedes._
import akka.actor.ActorSystem
import akka.stream._
import com.google.inject.Inject
import play.api.mvc._
import com.maalka.bedes.{BEDESTransform, BEDESTransformResult}
import akka.stream.scaladsl._
import akka.util.ByteString
import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}
import models.Measure
import org.apache.poi.ss.util.WorkbookUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
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
//  implicit val systemReads = Json.reads[models.System]
//  implicit val systemWrites = Json.writes[models.System]

  def buildXlsx = Action.async(parse.json) { request =>

    val systems = (request.body \ "systems").as[List[JsObject]]
    Logger.info("oo: " + systems)

    val systemTypes = systems.groupBy(_.fields.head._1)
    Logger.info("systemTypes: " + systemTypes)

    val workbook: XSSFWorkbook = new XSSFWorkbook


    // Measures Sheet
    val sheet1 = workbook.createSheet("Measures")

    val fieldNames = List("systemType", "detail", "implementationStatus", "startDate", "endDate", "comment")

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
    systemTypes.map { g =>
      val safeName = WorkbookUtil.createSafeSheetName(g._1)
      val sheet2 = workbook.createSheet(safeName)
      val headerRow = sheet2.createRow(0)

      // build header
      val distinctFields: List[List[String]] = g._2.flatMap { s =>
        s.fields.toList.flatMap { field =>
          val o = buildPath(field._2, List.empty[String])
          o.keys.toList
        }
      }.distinct

      distinctFields.zipWithIndex.foreach {
        case (f, i) => {
          val cell = headerRow.createCell(i)
          cell.setCellValue(f.reverse.mkString("-"))
        }
      }

      g._2.zipWithIndex.foreach{
        case (system, systemIndex) =>
         val systemFields: Seq[Map[List[String], Any]] = system.fields.map { field =>
           buildPath(field._2, List.empty[String])
         }

        Logger.info("systemFields: " + systemFields)

        // fill rows
        systemFields.map { sf =>
            val dataRow = sheet2.createRow(systemIndex+1)
            distinctFields.zipWithIndex.foreach {
              case (df, cellIndex) => {
                val cell = dataRow.createCell(cellIndex)
                sf.get(df).map { cellValue =>
                  cellValue match {
                    case c: String => cell.setCellValue(c)
                    case c: Int => cell.setCellValue(c)
                    case c: Boolean => cell.setCellValue(c)
                  }
              }
            }
          }
        }

      }
    }

    val ff = new File("workbook.xlsx")
    val fileOut: OutputStream = new FileOutputStream(ff)

    workbook.write(fileOut)

    fileOut.flush()
    fileOut.close()


    val dataContent: Sink[ByteString, _] = StreamConverters.fromOutputStream(() => fileOut)



    Future {
      Ok("").withHeaders(
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

  def parseCsv = Action.async(parse.multipartFormData) { request =>

      val result: Option[Future[Result]] = for {
        uploadType <- request.body.dataParts.get("type")
        uploadFile <- request.body.file("inputData")
      } yield {
        uploadType.headOption match {
          case Some("measures") => {
            val reader = CSVReader.open(uploadFile.ref.file)
            Future { Ok(Json.toJson(readMeasures(reader.all())))}
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
        Measure(systemType, detail, implementationStatus, BigInt(startDate), BigInt(endDate), Some(comments))
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

