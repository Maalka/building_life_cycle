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
