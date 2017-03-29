/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */

package models

import org.joda.time.DateTime

trait MeterData {
  var guid: Option[String]
  val owner: Option[String]
  val meterDataBlockId: String
  val startTime: Option[DateTime]
  val endTime: Option[DateTime]
  val K: Option[Double]
  val usage: Option[Double]
  val stringValue: Option[String]
  val cost: Option[Double]
  val estimatedValue: Option[Boolean]
  var tags: List[String]

  def isWeekday: Boolean = startTime match {
    case Some(v) => v.dayOfWeek.get <= 5
    case None => throw new Exception("No startTime defined")
  }

  def hourTimestamp = startTime match {
    case Some(v) => v.withMinuteOfHour(0).getMillis
    case None => throw new Exception("No startTime defined")
  }
}


case class MaalkaMeterData(var guid: Option[String],
   owner: Option[String],
   meterDataBlockId: String,
   startTime: Option[DateTime] = None,
   endTime: Option[DateTime] = None,
   K: Option[Double] = None,
   usage: Option[Double],
   stringValue: Option[String] = None,
   cost: Option[Double],
   estimatedValue: Option[Boolean],
   var tags: List[String] = List()) extends MeterData
