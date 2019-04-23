package configuration

import com.typesafe.config.Config
import play.api.libs.json.{Json, OFormat}

import scala.util.Try

case class KafkaConfiguration(
  bootstrapServers: List[String],
  sslEnabled: Boolean,
  schemaRegistry: List[String],
  topicsList: List[String]
)

object KafkaConfiguration {
  implicit val kafkaConfigurationFormat: OFormat[KafkaConfiguration] = Json.format[KafkaConfiguration]

  def parse(config: Config): Try[KafkaConfiguration] =
    for {
      bootstrapServers <- ConfigParser.parse[List[String]]("kafka.bootstrap-servers", config)
      sslEnabled <- ConfigParser.parse[Boolean]("kafka.ssl-enabled", config)
      schemaRegistry <- ConfigParser.parse[List[String]]("kafka.schema-registry", config)
      topicsList <- ConfigParser.parse[List[String]]("kafka.topics-list", config)
    } yield KafkaConfiguration(bootstrapServers, sslEnabled, schemaRegistry, topicsList)
}
