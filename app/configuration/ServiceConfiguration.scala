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

  def environmentConfigurableProperties(): EnvironmentConfigurableProperties

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

object ServiceConfiguration {
  def envValue(name: String)(implicit serviceConfiguration: ServiceConfiguration): Try[String] =
    serviceConfiguration
      .environmentVariables()
      .get(name)
      .fold[Try[String]](Failure(UndefinedEnvValueException(name)))(Success.apply)
}
