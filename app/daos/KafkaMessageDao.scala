package daos

import services.messaging.KafkaMessage

import scala.concurrent.{ExecutionContext, Future}

trait KafkaMessageDao {
  def insert(kafkaMessage: KafkaMessage)(implicit executionContext: ExecutionContext): Future[KafkaMessage]

  def getAll(page: Int)(implicit executionContext: ExecutionContext): Future[Seq[KafkaMessage]]
}
