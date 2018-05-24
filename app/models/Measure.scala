package models

import org.joda.time.LocalDate

case class Measure(systemType: String,
                   detail: String,
                   implementationStatus: String,
                   startDate: LocalDate,
                   endDate: LocalDate,
                   comment: Option[String],
                   buildingName: Option[String],
                   buildingAddress: Option[String])

