package services.messaging
import akka.Done
import akka.actor.Cancellable
import akka.stream.scaladsl.Source

import scala.concurrent.{ExecutionContext, Future}

trait MessagingService {
  def init(topicNames: List[String])(implicit executionContext: ExecutionContext): Future[Done]

  def subscribe()(implicit executionContext: ExecutionContext): Source[Seq[KafkaMessage], Cancellable]

  def getAll(page: Int)(implicit executionContext: ExecutionContext): Future[Seq[KafkaMessage]]
}
