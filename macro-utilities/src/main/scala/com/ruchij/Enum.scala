package com.ruchij

trait Enum[A <: Enum[A]] {
  self =>

  val key: String = Enum.enumKey(self)
}

object Enum {
  def enumKey(enum: Enum[_]): String =
    enum.getClass.getSimpleName.replaceAll("\\$", "")
}
