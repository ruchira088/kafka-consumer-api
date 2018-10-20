package configuration

import json.JsonFormats.DateTimeFormat
import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

case class ServiceInformation(
  serviceName: String,
  serviceVersion: String,
  currentTimestamp: DateTime,
  javaVersion: String,
  sbtVersion: String,
  scalaVersion: String
)

object ServiceInformation {
  implicit val serviceInformationFormat: OFormat[ServiceInformation] = Json.format[ServiceInformation]
}
