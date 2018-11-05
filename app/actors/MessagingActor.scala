package actors
import akka.Done
import akka.actor.{Actor, ActorRef, Cancellable, Props}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{KillSwitches, Materializer}
import com.typesafe.scalalogging.Logger
import play.api.libs.json.Json.{toJson => json}
import play.api.libs.json.{JsValue, Json}
import services.messaging.{KafkaMessage, MessagingService}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

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

  def start()(implicit executionContext: ExecutionContext): Cancellable = {
    val messageCancellable = messagingService
      .subscribe()
      .viaMat(KillSwitches.single)(Keep.left)
      .toMat(sink)(Keep.left)
      .run()

    val heartBeatCancellable =
      MessagingActor
        .heartBeat()
        .viaMat(KillSwitches.single)(Keep.left)
        .toMat(Sink.foreach(sender ! _))(Keep.left)
        .run()

    new Cancellable {
      override def cancel(): Boolean = messageCancellable.cancel() && heartBeatCancellable.cancel()
      override def isCancelled: Boolean = messageCancellable.isCancelled && heartBeatCancellable.isCancelled
    }
  }

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

  def heartBeat(): Source[JsValue, Cancellable] =
    Source.tick(initialDelay = 5 seconds, interval = 30 seconds, tick = Json.obj("isHeartBeat" -> true))
}
