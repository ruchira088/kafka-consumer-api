package utils
import exceptions.DataValidationException

import scala.util.{Failure, Success, Try}

object Parsers {
  def booleanParser(value: String): Try[Boolean] =
    value.toLowerCase match {
      case "true" => Success(true)
      case "false" => Success(false)
      case _ => Failure(DataValidationException(s"Unable to parse $value as a Boolean"))
    }
}
