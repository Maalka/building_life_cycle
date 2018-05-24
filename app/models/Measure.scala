package models

case class Measure(systemType: String,
                   detail: String,
                   implementationStatus: String,
                   startDate: BigInt,
                   endDate: BigInt,
                   comment: Option[String],
                   buildingName: Option[String],
                   buildingAddress: Option[String])

