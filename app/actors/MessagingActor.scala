package actors
import akka.actor.{Actor, ActorRef, Props}
import akka.stream.Materializer
import services.messaging.MessagingService

import scala.concurrent.ExecutionContext

class MessagingActor(messagingService: MessagingService, sender: ActorRef)(
  implicit executionContext: ExecutionContext,
  materializer: Materializer
) extends Actor {
  override def receive: Receive = {
    case _ =>
      messagingService.subscribe().runForeach {
        sender ! _
      }
  }
}

object MessagingActor {
  def props(
    messagingService: MessagingService,
    sender: ActorRef
  )(implicit executionContext: ExecutionContext, materializer: Materializer): Props =
    Props(new MessagingActor(messagingService, sender))
}
