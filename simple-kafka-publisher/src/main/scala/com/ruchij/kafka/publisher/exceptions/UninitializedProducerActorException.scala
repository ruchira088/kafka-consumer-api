package com.ruchij.kafka.publisher.exceptions

case object UninitializedProducerActorException extends Exception {
  override def getMessage: String = "The producer actor has NOT been initialized"
}
