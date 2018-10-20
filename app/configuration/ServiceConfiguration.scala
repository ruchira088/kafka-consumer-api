package configuration
import java.util.UUID

import com.ruchij.eed3si9n.BuildInfo
import org.joda.time.DateTime

import scala.util.Properties

trait ServiceConfiguration {
  def environmentVariables(): Map[String, String] = sys.env

  def currentTimestamp(): DateTime = DateTime.now()

  def uuid(): UUID = UUID.randomUUID()

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

object ServiceConfiguration extends ServiceConfiguration
