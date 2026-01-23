package app.betr.kafka_challenge

import io.vertx.core.Future
import io.vertx.core.VerticleBase

class MainVerticle : VerticleBase() {

  override fun start() : Future<*> {
    return if (System.getenv("ROLE") == "producer") {
      vertx.deployVerticle(KafkaProducerVerticle())
    } else {
      vertx.deployVerticle(KafkaConsumerVerticle())
    }
  }
}
