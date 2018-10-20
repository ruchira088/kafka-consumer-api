package json
import play.api.libs.json.{JsError, JsResult, JsSuccess}

import scala.util.Try

object JsonUtils {
  def jsonResult[A](tryResult: Try[A]): JsResult[A] =
    tryResult.fold(throwable => JsError(throwable.getMessage), result => JsSuccess(result))
}
