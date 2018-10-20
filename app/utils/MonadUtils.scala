package utils
import scala.util.{Failure, Success, Try}

object MonadUtils {
  def fromOption[A](exception: Exception)(option: Option[A]): Try[A] =
    option.fold[Try[A]](Failure(exception))(Success.apply)
}
