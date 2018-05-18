package models

import org.joda.time.{DateTime, LocalDate}

case class Measure(systemType: String, detail: String, implementationStatus: String, startDate: BigInt, endDate: BigInt, comment: Option[String])

