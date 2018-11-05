package configuration

import configuration.EnvNames._
import json.JsonFormats.FiniteDurationWrites
import play.api.libs.json.{Json, Writes}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Success, Try}

case class EnvironmentConfigurableProperties(
  topicsList: List[String],
  databasePollInterval: FiniteDuration,
  queryPageSize: Int,
  webSocketBufferSize: Int
)

object EnvironmentConfigurableProperties {
  val DEFAULT = EnvironmentConfigurableProperties(
    topicsList = List("SAMPLE_TOPIC"),
    databasePollInterval = 100 milliseconds,
    queryPageSize = 100,
    webSocketBufferSize = 128
  )

  implicit val environmentConfigurablePropertiesWrites: Writes[EnvironmentConfigurableProperties] =
    Json.writes[EnvironmentConfigurableProperties]

  def parse(environmentVariables: Map[String, String]): Try[EnvironmentConfigurableProperties] =
    for {
      topics <- envValueAsStringList(environmentVariables, KAFKA_TOPICS, DEFAULT.topicsList)
      queryPageSize <- envValueAsInt(environmentVariables, QUERY_PAGE_SIZE, DEFAULT.queryPageSize)
      databasePollInterval <- envValueAsSeconds(
        environmentVariables,
        DATABASE_POLL_INTERVAL,
        DEFAULT.databasePollInterval
      )
      webSocketBufferSize <- envValueAsInt(environmentVariables, WEB_SOCKET_BUFFER_SIZE, DEFAULT.webSocketBufferSize)
    } yield EnvironmentConfigurableProperties(topics, databasePollInterval, queryPageSize, webSocketBufferSize)

  def envValueAsInt(environmentVariables: Map[String, String], envName: String, default: Int): Try[Int] =
    environmentVariables
      .get(envName)
      .fold[Try[Int]](Success(default)) { string =>
        Try(Integer.parseInt(string))
      }

  def envValueAsStringList(
    environmentVariables: Map[String, String],
    envName: String,
    default: List[String]
  ): Try[List[String]] =
    environmentVariables.get(envName).fold(Success(default)) { list =>
      Success(list.split('=').toList)
    }

  def envValueAsSeconds(
    environmentVariables: Map[String, String],
    envName: String,
    default: FiniteDuration
  ): Try[FiniteDuration] =
    envValueAsInt(environmentVariables, envName, default.toMillis.toInt).map(_ milliseconds)
}
