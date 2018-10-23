package utils
import java.util.Locale

import com.github.javafaker.Faker
import play.api.libs.json.{JsObject, Json, OFormat}

object RandomGenerator {
  val faker: Faker = Faker.instance(Locale.ENGLISH)

  import faker._

  private case class JsonMessage(quote: String, location: String)
  private implicit val jsonMessageFormat: OFormat[JsonMessage] = Json.format[JsonMessage]

  def topicName(): String = rickAndMorty().character()

  def username(): String = name().username()

  def jsonMessage(): JsObject =
    Json.toJsObject {
      JsonMessage(rickAndMorty().quote(), rickAndMorty().location())
    }
}
