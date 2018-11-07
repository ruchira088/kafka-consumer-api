import com.ruchij.EnumParser
import configuration.EnvironmentType

object Playground {
  def main(args: Array[String]): Unit = {
    println(EnumParser.parse[EnvironmentType]("LocalDev").get)
  }
}
