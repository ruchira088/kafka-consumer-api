package exceptions

case class DataValidationException(errorMessage: String) extends Exception {
  override def getMessage: String = errorMessage
}
