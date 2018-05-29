package models

import org.joda.time.LocalDate

case class Measure(
                  buildingName: Option[String],
                  buildingAddress: Option[String],
                  systemType: String,
                  detail: String,
                  implementationStatus: String,
                  startDate: LocalDate,
                  endDate: LocalDate,
                  comment: Option[String]
)

