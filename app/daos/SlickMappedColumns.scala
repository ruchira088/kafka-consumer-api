package daos
import java.sql.Timestamp

import daos.SlickMappedColumns.JsonParseError
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{JsValue, Json, OFormat}
import slick.jdbc.JdbcProfile

import scala.util.Try

trait SlickMappedColumns {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  implicit val dateTimeColumnType: BaseColumnType[DateTime] =
    MappedColumnType.base[DateTime, Timestamp](
      dateTime => new Timestamp(dateTime.getMillis),
      timestamp => new DateTime(timestamp.getTime)
    )

  implicit val jsonColumnType: BaseColumnType[JsValue] =
    MappedColumnType.base[JsValue, String](
      Json.stringify,
      jsonString =>
        Try(Json.parse(jsonString))
          .fold(throwable => Json.toJsObject(JsonParseError(throwable.getMessage)), identity)
    )
}

object SlickMappedColumns {
  case class JsonParseError(error: String)

  implicit val jsonParseErrorFormat: OFormat[JsonParseError] = Json.format[JsonParseError]
}
