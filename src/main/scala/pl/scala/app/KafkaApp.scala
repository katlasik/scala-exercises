package pl.scala.app

/**
  * Create application that:
  *
  * 1. Takes communicates via websockets. When it receives message it puts it's in kafka topic.
  * 2. After message is pushed to Kafka topic application reads it and puts it into messages table.
  */

object KafkaApp extends App {}
