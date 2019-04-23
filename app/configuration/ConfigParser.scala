package configuration

import java.util.concurrent.TimeUnit

import com.typesafe.config.Config

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

trait ConfigParser[+A] {
  def parse(path: String, config: Config): Try[A]
}

object ConfigParser {
  def parse[A](path: String, config: Config)(implicit configParser: ConfigParser[A]): Try[A] =
    configParser.parse(path, config)

  implicit val stringConfigParser: ConfigParser[String] =
    (path: String, config: Config) => Try(config.getString(path))

  implicit val stringListConfigParser: ConfigParser[List[String]] =
    (path: String, config: Config) => stringConfigParser.parse(path, config).map(_.split(",").map(_.trim).toList)

  implicit val intConfigParser: ConfigParser[Int] = (path: String, config: Config) => Try(config.getInt(path))

  implicit val finiteDurationConfigParser: ConfigParser[FiniteDuration] =
    (path: String, config: Config) =>
      Try(config.getDuration(path)).map(duration => FiniteDuration(duration.toMillis, TimeUnit.MILLISECONDS))

  implicit val booleanConfigParser: ConfigParser[Boolean] =
    (path: String, config: Config) => Try(config.getBoolean(path))
}
