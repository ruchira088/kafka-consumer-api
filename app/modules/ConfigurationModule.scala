package modules

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.stream.Materializer
import com.google.inject.{AbstractModule, Provides}
import com.ruchij.eed3si9n.BuildInfo
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import configuration.{KafkaConfiguration, OtherConfigurations, ServiceConfiguration}
import daos.{KafkaMessageDao, KafkaMessageSlickDao}
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer}
import kafka.{KafkaConsumer, KafkaConsumerImpl}
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.common.security.auth.SecurityProtocol
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.{Json, Writes}
import services.messaging.{MessagingService, MessagingServiceImpl}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

class ConfigurationModule extends AbstractModule {
  private val logger = Logger[ConfigurationModule]

  override def configure(): Unit = {
    val config = ConfigFactory.load()

    val serviceConfiguration =
      new ServiceConfiguration {
        override def otherConfigurations(): OtherConfigurations =
          OtherConfigurations.parse(config).get

        override def kafkaConfiguration(): KafkaConfiguration =
          KafkaConfiguration.parse(config).get
      }

    logger.info(s"Service Information: ${prettyPrint(serviceConfiguration.serviceInformation())}")
    logger.info(s"Kafka Configurations: ${prettyPrint(serviceConfiguration.kafkaConfiguration())}")
    logger.info(s"Other Configurations: ${prettyPrint(serviceConfiguration.otherConfigurations())}")
    logger.info(s"Environment Variables: ${prettyPrint(serviceConfiguration.environmentVariables())}")

    bind(classOf[ServiceConfiguration]).toInstance(serviceConfiguration)
    bind(classOf[KafkaConsumer]).to(classOf[KafkaConsumerImpl])
    bind(classOf[KafkaMessageDao]).to(classOf[KafkaMessageSlickDao])
  }

  @Provides
  def messagingService(kafkaConsumer: KafkaConsumer, kafkaMessageDao: KafkaMessageDao)(
    implicit materializer: Materializer,
    executionContext: ExecutionContext,
    serviceConfiguration: ServiceConfiguration
  ): MessagingService =
    new MessagingServiceImpl(kafkaConsumer, kafkaMessageDao) {
      init(serviceConfiguration.kafkaConfiguration().topicsList)
    }

  @Provides
  def kafkaConsumerSettings(
    implicit serviceConfiguration: ServiceConfiguration,
    actorSystem: ActorSystem
  ): ConsumerSettings[String, AnyRef] =
    ConsumerSettings(
      actorSystem,
      new StringDeserializer,
      new KafkaAvroDeserializer(null, ConfigurationModule.avroConfiguration.asJava)
    ).withGroupId(BuildInfo.name)
      .withBootstrapServers(serviceConfiguration.kafkaConfiguration().bootstrapServers.mkString(","))
      .withProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, ConfigurationModule.kafkaSecurityProtocol.name)
      .withMaxWakeups(0)

  private def prettyPrint[A](value: A)(implicit writes: Writes[A]): String =
    Json.prettyPrint(Json.toJson(value))
}

object ConfigurationModule {
  def avroConfiguration(implicit serviceConfiguration: ServiceConfiguration): Map[String, String] =
    Map(
      AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG ->
        serviceConfiguration.kafkaConfiguration().schemaRegistry.mkString(",")
    )

  def kafkaSecurityProtocol(implicit serviceConfiguration: ServiceConfiguration): SecurityProtocol =
    if (serviceConfiguration.kafkaConfiguration().sslEnabled) SecurityProtocol.SSL else SecurityProtocol.PLAINTEXT
}
