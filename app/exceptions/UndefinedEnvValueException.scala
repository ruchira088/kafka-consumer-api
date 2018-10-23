package exceptions

case class UndefinedEnvValueException(envName: String) extends Exception {
  override def getMessage: String = s"$envName is NOT defined as an environment variable"
}
