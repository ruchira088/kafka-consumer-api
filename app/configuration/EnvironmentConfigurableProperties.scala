package configuration

import configuration.EnvNames._
import json.JsonFormats.FiniteDurationWrites
import play.api.libs.json.{Json, Writes}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Success, Try}

case class EnvironmentConfigurableProperties(databasePollInterval: FiniteDuration, queryPageSize: Int)

object EnvironmentConfigurableProperties {
  implicit val environmentConfigurablePropertiesWrites: Writes[EnvironmentConfigurableProperties] =
    Json.writes[EnvironmentConfigurableProperties]

  def parse(environmentVariables: Map[String, String]): Try[EnvironmentConfigurableProperties] =
    for {
      queryPageSize <- envValueAsInt(environmentVariables, QUERY_PAGE_SIZE, default = 100)
      databasePollInterval <- envValueAsSeconds(environmentVariables, DATABASE_POLL_INTERVAL, default = 2 seconds)
    } yield EnvironmentConfigurableProperties(databasePollInterval, queryPageSize)

  def envValueAsInt(environmentVariables: Map[String, String], envName: String, default: Int): Try[Int] =
    environmentVariables
      .get(envName)
      .fold[Try[Int]](Success(default)) { string =>
        Try(Integer.parseInt(string))
      }

  def envValueAsSeconds(
    environmentVariables: Map[String, String],
    envName: String,
    default: FiniteDuration
  ): Try[FiniteDuration] =
    envValueAsInt(environmentVariables, envName, default.toSeconds.toInt).map(_ seconds)
}
