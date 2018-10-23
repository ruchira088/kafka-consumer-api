package kafka

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerMessage, ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Source
import javax.inject.{Inject, Singleton}

@Singleton
class KafkaConsumerImpl @Inject()(consumerSettings: ConsumerSettings[String, AnyRef]) extends KafkaConsumer {
  override def subscribe(topics: String*): Source[ConsumerMessage.CommittableMessage[String, AnyRef], _] =
    Consumer.committableSource(consumerSettings, Subscriptions.topics(topics: _*))
}
