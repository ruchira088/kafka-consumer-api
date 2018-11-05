package com.ruchij

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.util.Try

object EnumParser {
  def parse[A <: Enum[A]](value: String): Try[A] = macro parseImpl[A]

  def parseImpl[A](c: blackbox.Context)(value: c.Expr[String])(implicit wTypeTag: c.WeakTypeTag[A]): c.universe.Tree = {
    import c.universe._

    val subClasses = wTypeTag.tpe.typeSymbol.asClass.knownDirectSubclasses

    val mappings: Set[c.universe.Tree] =
      subClasses.map { symbol =>
        cq"""${c.mirror.staticModule(symbol.fullName)}.key =>
            scala.util.Success(${c.mirror.staticModule(symbol.fullName)})"""
      }

    val possibleKeys: Set[c.universe.Tree] =
      subClasses.map { symbol =>
        q"""${c.mirror.staticModule(symbol.fullName)}.key"""
      }

    val enumType = wTypeTag.tpe.typeSymbol.asClass.name.toString

    q"""
       $value.toLowerCase match {
          case ..$mappings
          case _ => scala.util.Failure(com.ruchij.EnumParseException($enumType, $value, $possibleKeys.toList.sorted))
       }
     """
  }
}
