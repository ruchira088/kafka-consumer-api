package configuration

import com.typesafe.config.Config
import json.JsonFormats.FiniteDurationWrites
import play.api.libs.json.{Json, Writes}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

case class OtherConfigurations(databasePollInterval: FiniteDuration, queryPageSize: Int, webSocketBufferSize: Int)

object OtherConfigurations {
  implicit val environmentConfigurablePropertiesWrites: Writes[OtherConfigurations] =
    Json.writes[OtherConfigurations]

  def parse(config: Config): Try[OtherConfigurations] =
    for {
      databasePollInterval <- ConfigParser.parse[FiniteDuration]("database.polling-interval", config)
      queryPageSize <- ConfigParser.parse[Int]("database.query-page-size", config)
      webSocketBufferSize <- ConfigParser.parse[Int]("web-sockets.buffer-size", config)
    } yield OtherConfigurations(databasePollInterval, queryPageSize, webSocketBufferSize)
}
