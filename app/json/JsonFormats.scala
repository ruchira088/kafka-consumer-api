package json

import exceptions.DataValidationException
import json.JsonUtils.jsonResult
import org.joda.time.DateTime
import play.api.libs.json._

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Try}

object JsonFormats {
  implicit object DateTimeFormat extends Format[DateTime] {
    override def writes(dateTime: DateTime): JsValue = JsString(dateTime.toString)

    override def reads(json: JsValue): JsResult[DateTime] =
      jsonResult {
        json match {
          case JsString(value) => Try(DateTime.parse(value))
          case _ => Failure(DataValidationException("Must be a JSON string type"))
        }
      }
  }

  implicit object FiniteDurationWrites extends Writes[FiniteDuration] {
    override def writes(finiteDuration: FiniteDuration): JsValue =
      JsString(finiteDuration.toString())
  }
}
