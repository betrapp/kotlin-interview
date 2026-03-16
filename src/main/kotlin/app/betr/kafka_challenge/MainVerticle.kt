package app.betr.kafka_challenge

import io.vertx.core.Future
import io.vertx.core.VerticleBase

class MainVerticle : VerticleBase() {

  override fun start() : Future<*> {

    val players = mutableListOf(
      Player("amy", 100, 5, 1647012000000),
      Player("david", 100, 5, 1647010800000),
      Player("heraldo", 50, 10, 1647015600000),
      Player("aakansha", 75, 3, 1647013200000),
      Player("aleksa", 150, 8, 1647009600000),
      Player("charlie", 150, 8, 1647011400000),
      Player("bob", 100, 7, 1647014400000),
      Player("diana", 75, 3, 1647016200000)
    )

    // TODO: Call your sorting function here
    val sortedPlayers = sortPlayers(players)

    sortedPlayers.forEach { player ->
      println("${player.name} ${player.totalScore} ${player.winStreak} ${player.lastWinTimestamp}")
    }

    return Future.succeededFuture<Void>()
//    return if (System.getenv("ROLE") == "producer") {
//      vertx.deployVerticle(KafkaProducerVerticle())
//    } else {
//      vertx.deployVerticle(KafkaConsumerVerticle())
//    }
  }
}

data class Player(
  val name: String,
  val totalScore: Int,
  val winStreak: Int,
  val lastWinTimestamp: Long
)

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * CODING CHALLENGE: Tournament Leaderboard Sorting
 * ═══════════════════════════════════════════════════════════════════════════════
 *
*  Your task is to implement
 * a sorting algorithm that ranks players according to complex business rules.
 *
 * 📋 SORTING RULES (in order of priority):
 *
 * 1. PRIMARY: Sort by totalScore in DESCENDING order (highest first)
 *
 * 2. TIE-BREAKER #1: If totalScore is equal, sort by winStreak in DESCENDING order
 *    (players with longer winning streaks rank higher)
 *
 * 3. TIE-BREAKER #2: If both totalScore AND winStreak are equal,
 *    sort by lastWinTimestamp in ASCENDING order
 *    (earlier timestamp = longer time since last win)
 *
 * 4. TIE-BREAKER #3: If all above are equal, sort by name in ASCENDING order
 *    (alphabetically A-Z)
 *
 * 📤 EXPECTED OUTPUT:
 *   aleksa 150 8 1647009600000
 *   charlie 150 8 1647011400000
 *   bob 100 7 1647014400000
 *   amy 100 5 1647012000000
 *   david 100 5 1647010800000
 *   aakansha 75 3 1647013200000
 *   diana 75 3 1647016200000
 *   heraldo 50 10 1647015600000
 *
 * 💡 EXPLANATION OF EXPECTED OUTPUT:
 * - aleksa & charlie: Both 150 points, 8 streak → aleksa earlier timestamp
 * - bob, amy, david: All 100 points → bob has 7 streak (higher than 5)
 * - amy & david: Same score & streak → david has earlier timestamp
 * - aakansha & diana: Both 75 points, 3 streak → aakansha earlier timestamp
 * - heraldo: Lowest score (50) despite high streak
 *
 * 🎯 HINTS:
 * - Think about how to handle multiple comparison criteria
 * - Write helper functions to keep your code clean
 *
 */

fun sortPlayers(players: MutableList<Player>): List<Player> {
    // TODO: Implement your sorting algorithm here

    return players
}
