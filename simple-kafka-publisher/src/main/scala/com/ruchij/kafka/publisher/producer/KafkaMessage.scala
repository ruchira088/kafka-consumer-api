package com.ruchij.kafka.publisher.producer
import java.util.Locale

import com.github.javafaker.Faker
import com.sksamuel.avro4s.RecordFormat

case class KafkaMessage(chuckNorrisFact: String, rickAndMortyQuote: String, email: String)

object KafkaMessage {
  val faker: Faker = Faker.instance(Locale.ENGLISH)

  val recordFormat: RecordFormat[KafkaMessage] = RecordFormat[KafkaMessage]

  def random(): KafkaMessage =
    KafkaMessage(faker.chuckNorris().fact(), faker.rickAndMorty().quote(), faker.internet().emailAddress())
}
