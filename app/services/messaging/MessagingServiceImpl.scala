package services.messaging
import akka.Done
import akka.actor.Cancellable
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.scalalogging.Logger
import configuration.ServiceConfiguration
import daos.KafkaMessageDao
import javax.inject.{Inject, Singleton}
import kafka.KafkaConsumer
import org.joda.time.DateTime
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future.fromTry
import scala.concurrent.duration._
import scala.collection.mutable
import scala.language.postfixOps
import scala.util.Try

@Singleton
class MessagingServiceImpl @Inject()(kafkaConsumer: KafkaConsumer, kafkaMessageDao: KafkaMessageDao)(
  implicit materializer: Materializer,
  serviceConfiguration: ServiceConfiguration
) extends MessagingService {

  private val logger = Logger[MessagingServiceImpl]

  override def init(topicNames: List[String])(implicit executionContext: ExecutionContext): Future[Done] =
    kafkaConsumer
      .subscribe(topicNames: _*)
      .mapAsync(parallelism = 10) {
        case CommittableMessage(record, committableOffset) =>
          for {
            json <- fromTry(Try(Json.parse(record.value().toString)))

            kafkaMessage <- kafkaMessageDao.insert {
              KafkaMessage(
                record.topic(),
                serviceConfiguration.currentTimestamp(),
                record.key(),
                json,
                record.partition(),
                record.offset()
              )
            }

          } yield committableOffset
      }
      .mapAsync(parallelism = 1)(_.commitScaladsl())
      .runWith(Sink.ignore)

  override def subscribe()(implicit executionContext: ExecutionContext): Source[Seq[KafkaMessage], Cancellable] = {
    logger.info("New MessageServiceImpl subscription")

    val sentMessages = mutable.Set.empty[KafkaMessage]

    Source
      .tick(
        initialDelay = 0 seconds,
        interval = serviceConfiguration.environmentConfigurableProperties().databasePollInterval,
        tick = (): Unit
      )
      .mapAsync(parallelism = 1) { _ =>
        getAll(0)
      }
      .map {
        _.filter { kafkaMessage =>
          !sentMessages.contains(kafkaMessage)
        }
      }
      .map { kafkaMessages =>
        sentMessages.synchronized {
          sentMessages ++= kafkaMessages
        }
        kafkaMessages.reverse
      }
  }

  override def getAll(page: Int)(implicit executionContext: ExecutionContext): Future[Seq[KafkaMessage]] =
    kafkaMessageDao.getAll(page)
}
