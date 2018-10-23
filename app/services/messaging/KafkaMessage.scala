package services.messaging

import json.JsonFormats.DateTimeFormat
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json, OFormat}

case class KafkaMessage(
  topicName: String,
  receivedAt: DateTime,
  key: String,
  value: JsValue,
  partition: Int,
  offset: Long
)

object KafkaMessage {
  implicit val kafkaMessageFormat: OFormat[KafkaMessage] = Json.format[KafkaMessage]
}
