package kafka
import akka.kafka.ConsumerMessage
import akka.stream.scaladsl.Source

trait KafkaConsumer {
  def subscribe(topics: String*): Source[ConsumerMessage.CommittableMessage[String, AnyRef], _]
}
