package daos

import configuration.ServiceConfiguration
import javax.inject.{Inject, Singleton}
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.JsValue
import services.messaging.KafkaMessage
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class KafkaMessageSlickDao @Inject()(val dbConfigProvider: DatabaseConfigProvider)(
  implicit serviceConfiguration: ServiceConfiguration
) extends KafkaMessageDao
    with HasDatabaseConfigProvider[JdbcProfile]
    with SlickMappedColumns {

  import profile.api._

  class KafkaMessageTable(tag: Tag) extends Table[KafkaMessage](tag, "kafka_messages") {

    def topicName: Rep[String] = column[String]("topic_name")

    def receivedAt: Rep[DateTime] = column[DateTime]("received_at")

    def key: Rep[String] = column[String]("key")

    def value: Rep[JsValue] = column[JsValue]("value")

    def partition: Rep[Int] = column[Int]("partition")

    def offset: Rep[Long] = column[Long]("message_offset")

    override def * : ProvenShape[KafkaMessage] =
      (topicName, receivedAt, key, value, partition, offset) <> (KafkaMessage.apply _ tupled, KafkaMessage.unapply)
  }

  val kafkaMessages = TableQuery[KafkaMessageTable]

  def insert(kafkaMessage: KafkaMessage)(implicit executionContext: ExecutionContext): Future[KafkaMessage] =
    db.run(kafkaMessages += kafkaMessage).map(_ => kafkaMessage)

  override def getAll(page: Int)(implicit executionContext: ExecutionContext): Future[Seq[KafkaMessage]] =
    db.run {
      kafkaMessages
        .sortBy(_.receivedAt.desc)
        .drop(page * serviceConfiguration.otherConfigurations().queryPageSize)
        .take(serviceConfiguration.otherConfigurations().queryPageSize)
        .result
    }
}
