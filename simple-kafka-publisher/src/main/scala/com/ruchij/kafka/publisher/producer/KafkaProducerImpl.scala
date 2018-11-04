package com.ruchij.kafka.publisher.producer

import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.scaladsl.{BroadcastHub, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.ruchij.kafka.publisher.exceptions.UninitializedProducerActorException
import org.apache.kafka.clients.producer.ProducerRecord

import scala.util.{Failure, Success, Try}

class KafkaProducerImpl(producerSettings: ProducerSettings[String, AnyRef])(
  implicit actorSystem: ActorSystem,
  actorMaterializer: ActorMaterializer
) extends KafkaProducer[ProducerRecord[String, AnyRef]] {

  private var producerActor: Option[ActorRef] = None

  override def tell[B <: ProducerRecord[String, AnyRef]](record: B): Try[B] =
    producerActor.fold[Try[B]](Failure(UninitializedProducerActorException)) { producer =>
      producer ! record
      Success(record)
    }

  def initialize(): Unit = {
    val (producer, source): (ActorRef, Source[ProducerRecord[String, AnyRef], _]) =
      Source
        .actorRef[ProducerRecord[String, AnyRef]](Integer.MAX_VALUE, OverflowStrategy.fail)
        .toMat(BroadcastHub.sink)(Keep.both)
        .run()

    source
      .map {
        ProducerMessage.Message(_, (): Unit)
      }
      .via(Producer.flexiFlow(producerSettings))
      .runWith(Sink.ignore)

    producerActor = Some(producer)
  }
}
