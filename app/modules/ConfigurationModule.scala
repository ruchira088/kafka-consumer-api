package modules

import java.util

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.scalalogging.Logger
import configuration.{EnvNames, EnvironmentConfigurableProperties, ServiceConfiguration}
import configuration.ServiceConfiguration.envValue
import daos.{KafkaMessageDao, KafkaMessageSlickDao}
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer}
import kafka.{KafkaConsumer, StubKafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.{Json, Writes}
import services.messaging.{MessagingService, MessagingServiceImpl}

import scala.collection.JavaConverters._
import scala.util.Try

class ConfigurationModule extends AbstractModule {
  private val logger = Logger[ConfigurationModule]

  override def configure(): Unit = {
    val serviceConfiguration =
      new ServiceConfiguration {
        override def environmentConfigurableProperties(): EnvironmentConfigurableProperties =
          EnvironmentConfigurableProperties.parse(environmentVariables()).get
      }

    logger.info(s"Service Information: ${prettyPrint(serviceConfiguration.serviceInformation())}")
    logger.info(s"Configurable Properties: ${prettyPrint(serviceConfiguration.environmentConfigurableProperties())}")
    logger.info(s"Environment Variables: ${prettyPrint(serviceConfiguration.environmentVariables())}")

    bind(classOf[ServiceConfiguration]).toInstance(serviceConfiguration)
    bind(classOf[MessagingService]).to(classOf[MessagingServiceImpl])
    bind(classOf[KafkaConsumer]).to(classOf[StubKafkaConsumer])
    bind(classOf[KafkaMessageDao]).to(classOf[KafkaMessageSlickDao])
  }

  @Provides
  def kafkaConsumerSettings(
    implicit serviceConfiguration: ServiceConfiguration,
    actorSystem: ActorSystem
  ): ConsumerSettings[String, AnyRef] =
    ConsumerSettings(
      actorSystem,
      new StringDeserializer,
      new KafkaAvroDeserializer(null, ConfigurationModule.kafkaAvroProps.get)
    )

  private def prettyPrint[A](value: A)(implicit writes: Writes[A]): String =
    Json.prettyPrint(Json.toJson(value))
}

object ConfigurationModule {
  def kafkaAvroProps(implicit serviceConfiguration: ServiceConfiguration): Try[util.Map[String, String]] =
    for {
      schemaRegistryUrl <- envValue(EnvNames.SCHEMA_REGISTRY_URL)
    } yield Map(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> schemaRegistryUrl).asJava
}
