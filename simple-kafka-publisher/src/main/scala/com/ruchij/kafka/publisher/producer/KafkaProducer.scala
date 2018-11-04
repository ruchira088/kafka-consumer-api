package com.ruchij.kafka.publisher.producer
import scala.util.Try

trait KafkaProducer[-A] {
  def tell[B <: A](record: B): Try[B]
}
