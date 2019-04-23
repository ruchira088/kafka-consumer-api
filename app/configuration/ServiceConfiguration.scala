package configuration
import java.util.UUID

import com.ruchij.eed3si9n.BuildInfo
import exceptions.UndefinedEnvValueException
import org.joda.time.DateTime

import scala.util.{Failure, Properties, Success, Try}

trait ServiceConfiguration {
  def environmentVariables(): Map[String, String] = sys.env

  def currentTimestamp(): DateTime = DateTime.now()

  def uuid(): UUID = UUID.randomUUID()

  def otherConfigurations(): OtherConfigurations

  def kafkaConfiguration(): KafkaConfiguration

  def serviceInformation(): ServiceInformation =
    ServiceInformation(
      BuildInfo.name,
      BuildInfo.version,
      currentTimestamp(),
      Properties.javaVersion,
      BuildInfo.sbtVersion,
      BuildInfo.scalaVersion
    )
}
