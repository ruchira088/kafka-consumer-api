package com.ruchij.kafka.publisher

import java.util.UUID

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.stream.ActorMaterializer
import com.ruchij.kafka.publisher.producer.{KafkaMessage, KafkaProducerImpl}
import com.ruchij.publisher.eed3si9n.BuildInfo
import configuration.{EnvNames, EnvironmentConfigurableProperties, ServiceConfiguration}
import io.confluent.kafka.serializers.KafkaAvroSerializer
import modules.ConfigurationModule
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.language.postfixOps

object App {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem(BuildInfo.name)
    implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

    implicit val serviceConfiguration: ServiceConfiguration = () => EnvironmentConfigurableProperties.DEFAULT

    val producerSettings =
      ProducerSettings(
        actorSystem,
        new StringSerializer,
        new KafkaAvroSerializer(null, ConfigurationModule.kafkaAvroProps.get)
      ).withBootstrapServers(ServiceConfiguration.envValue(EnvNames.KAFKA_BOOTSTRAP_SERVERS).get)

    val kafkaProducer = new KafkaProducerImpl(producerSettings) { initialize() }
    val topicName =
      EnvironmentConfigurableProperties.DEFAULT.topicsList.headOption
        .getOrElse(throw new Exception("Topics list is EMPTY !!!"))

    actorSystem.scheduler.schedule(1 second, 5 seconds) {
      kafkaProducer.tell {
        new ProducerRecord[String, AnyRef](
          topicName,
          UUID.randomUUID().toString,
          KafkaMessage.recordFormat.to(KafkaMessage.random())
        )
      }
    }
  }
}
