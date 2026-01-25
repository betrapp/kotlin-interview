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

/**
 * CODING CHALLENGE: Implement the Comparator
 *
 * Your task is to implement the compare() method to sort players according to these rules:
 *
 * 1. PRIMARY SORT: Sort by score in DESCENDING order (highest scores first)
 * 2. SECONDARY SORT: When scores are equal, sort by name in ASCENDING order (alphabetically A-Z)
 *
 * Expected output after sorting:
 *   aleksa 150
 *   amy 100
 *   david 100
 *   aakansha 75
 *   heraldo 50
 *
 * Note: "amy" comes before "david" because they have the same score (100),
 * so we fall back to alphabetical ordering by name.
 *
 * Hint: The compare() method should return:
 *   - A negative number if 'a' should come before 'b'
 *   - A positive number if 'a' should come after 'b'
 *   - Zero if they are equal
 */
class Checker : Comparator<Player> {
    override fun compare(a: Player, b: Player): Int {
        // TODO: Implement your solution here
        return 0
    }
}
