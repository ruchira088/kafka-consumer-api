package controllers
import actors.MessagingActor
import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import configuration.ServiceConfiguration
import controllers.responses.KafkaMessagesResponse
import javax.inject.{Inject, Singleton}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.{toJson => json}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import services.messaging.MessagingService

import scala.concurrent.ExecutionContext

@Singleton
class TopicController @Inject()(controllerComponents: ControllerComponents, messagingService: MessagingService)(
  implicit actorSystem: ActorSystem,
  materializer: Materializer,
  executionContext: ExecutionContext,
  serviceConfiguration: ServiceConfiguration
) extends AbstractController(controllerComponents) {

  def liveMessages(): WebSocket =
    WebSocket.accept[JsValue, JsValue] { _ =>
      ActorFlow.actorRef(
        sender => MessagingActor.props(messagingService, sender),
        bufferSize = serviceConfiguration.otherConfigurations().webSocketBufferSize,
        overflowStrategy = OverflowStrategy.fail
      )
    }

  def messages(page: Int): Action[AnyContent] =
    Action.async {
      messagingService
        .getAll(page)
        .map { kafkaMessages =>
          Ok {
            json(KafkaMessagesResponse(kafkaMessages))
          }
        }
    }
}
