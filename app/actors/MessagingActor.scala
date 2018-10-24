package actors
import akka.Done
import akka.actor.{Actor, ActorRef, Cancellable, Props}
import akka.stream.scaladsl.{Keep, Sink}
import akka.stream.{KillSwitches, Materializer}
import com.typesafe.scalalogging.Logger
import play.api.libs.json.Json.{toJson => json}
import services.messaging.{KafkaMessage, MessagingService}

import scala.concurrent.{ExecutionContext, Future}

sealed abstract class MessagingActor(messagingService: MessagingService, sender: ActorRef)(
  implicit materializer: Materializer
) extends Actor {

  private val logger = Logger[MessagingActor]

  val stop: Cancellable

  override def receive: Receive = {
    case _ =>
  }

  private val sink: Sink[Seq[KafkaMessage], Future[Done]] =
    Sink.foreach[Seq[KafkaMessage]] {
      _.foreach { kafkaMessage =>
        sender ! json(kafkaMessage)
      }
    }

  def start()(implicit executionContext: ExecutionContext): Cancellable =
    messagingService
      .subscribe()
      .viaMat(KillSwitches.single)(Keep.left)
      .toMat(sink)(Keep.left)
      .run()

  override def postStop(): Unit = {
    logger.info("Stopping web socket....")

    if (stop.cancel())
      logger.info("Successfully stopped web socket")
    else
      logger.warn("Unable to stop web socket")
  }
}

object MessagingActor {
  def props(
    messagingService: MessagingService,
    sender: ActorRef
  )(implicit executionContext: ExecutionContext, materializer: Materializer): Props =
    Props {
      new MessagingActor(messagingService, sender) {
        override val stop: Cancellable = start()
      }
    }
}
