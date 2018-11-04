package kafka

import java.util.concurrent.{CompletableFuture, CompletionStage}
import java.util.concurrent.atomic.AtomicLong

import akka.Done
import akka.kafka.ConsumerMessage
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset, GroupTopicPartition, PartitionOffset}
import akka.stream.scaladsl.Source
import configuration.ServiceConfiguration
import javax.inject.{Inject, Singleton}
import org.apache.kafka.clients.consumer.ConsumerRecord
import utils.RandomGenerator._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

@Singleton
class StubKafkaConsumer @Inject()(implicit serviceConfiguration: ServiceConfiguration) extends KafkaConsumer {
  private val offset = new AtomicLong(0)

  override def subscribe(topics: String*): Source[CommittableMessage[String, AnyRef], _] =
    Source
      .tick(initialDelay = 1 second, interval = StubKafkaConsumer.MESSAGE_INTERVAL, tick = (): Unit)
      .map { _ =>
        CommittableMessage(
          new ConsumerRecord[String, AnyRef](
            topicName(),
            Random.nextInt(StubKafkaConsumer.PARTITION_COUNT),
            offset.incrementAndGet(),
            serviceConfiguration.uuid().toString,
            jsonMessage()
          ),
          new CommittableOffset {
            override def partitionOffset: ConsumerMessage.PartitionOffset =
              PartitionOffset(
                GroupTopicPartition(username(), topicName(), Random.nextInt(StubKafkaConsumer.PARTITION_COUNT)),
                offset.get()
              )

            override def commitScaladsl(): Future[Done] = Future.successful(Done)

            override def commitJavadsl(): CompletionStage[Done] = CompletableFuture.completedFuture(Done)
          }
        )
      }
}

object StubKafkaConsumer {
  val PARTITION_COUNT: Int = 3

  val MESSAGE_INTERVAL: FiniteDuration = 2 seconds
}
