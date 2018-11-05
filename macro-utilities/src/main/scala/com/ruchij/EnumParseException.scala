package com.ruchij

case class EnumParseException(enumType: String, value: String, possibleKeys: List[String]) extends Exception {
  override def getMessage: String =
    s"""Unable to parse "$value" as $enumType. Possible keys are ${possibleKeys.mkString("(", ", ", ")")}"""
}
