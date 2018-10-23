package modules

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import com.google.inject.{AbstractModule, Provides}
import configuration.{EnvNames, ServiceConfiguration}
import configuration.ServiceConfiguration.envValue
import daos.{KafkaMessageDao, KafkaMessageSlickDao}
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer}
import kafka.{KafkaConsumer, StubKafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import services.messaging.{MessagingService, MessagingServiceImpl}

import scala.collection.JavaConverters._

class ConfigurationModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ServiceConfiguration]).toInstance(new ServiceConfiguration {})
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
      new KafkaAvroDeserializer(
        null,
        Map(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> envValue(EnvNames.SCHEMA_REGISTRY_URL).get).asJava
      )
    )
}
