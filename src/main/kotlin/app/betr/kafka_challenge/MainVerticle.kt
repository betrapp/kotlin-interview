package app.betr.kafka_challenge

import io.vertx.core.Future
import io.vertx.core.VerticleBase

class MainVerticle : VerticleBase() {

  override fun start() : Future<*> {

    val checker = Checker()

    val players = mutableListOf<Player>(
      Player("amy", 100),
      Player("david", 100),
      Player("heraldo", 50),
      Player("aakansha", 75),
      Player("aleksa", 150)
    )

    players.sortWith(checker)

    players.forEach { player ->
      println("${player.name} ${player.score}")
    }

    return Future.succeededFuture<Void>()
//    return if (System.getenv("ROLE") == "producer") {
//      vertx.deployVerticle(KafkaProducerVerticle())
//    } else {
//      vertx.deployVerticle(KafkaConsumerVerticle())
//    }
  }
}

data class Player(val name: String, val score: Int)

class Checker : Comparator<Player> {
    override fun compare(a: Player, b: Player): Int {
        // TODO: Implement the comparison logic here
        // 1. Compare by score in descending order (highest score first)
        // 2. If scores are equal, compare by name in ascending order (alphabetically)
        return 0

    }
}
