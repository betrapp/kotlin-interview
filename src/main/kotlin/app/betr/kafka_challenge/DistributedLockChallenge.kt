package app.betr.kafka_challenge

/**
 * INTERVIEW QUESTION: Distributed Locking
 *
 * Review the code below and answer the following:
 *
 * "This is a simplified distributed locking implementation. What problems might
 * occur in production, and how would you fix them?"
 *
 */

suspend fun processWithLock(entityId: String, block: suspend () -> Unit) {
    val lockKey = "lock:$entityId"
    val acquired = redis.setnx(lockKey, "locked")
    if (!acquired) return

    try {
        block()
    } finally {
        redis.del(lockKey)
    }
}
