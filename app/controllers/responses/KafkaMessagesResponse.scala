package controllers.responses
import play.api.libs.json.{Json, OFormat}
import services.messaging.KafkaMessage

case class KafkaMessagesResponse(messages: Seq[KafkaMessage])

object KafkaMessagesResponse {
  implicit val kafkaMessagesResponseFormat: OFormat[KafkaMessagesResponse] = Json.format[KafkaMessagesResponse]
}
