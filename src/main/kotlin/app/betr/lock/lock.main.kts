// This function prevents concurrent processing of the same entity across multiple service instances.
// Candidates can use Google/docs. Multiple instances may try to acquire the same lock simultaneously.
// Assume block() takes 100ms to 30 seconds to complete.
//
// Task:
// 1. List everything that can go wrong in production
// 2. Pick the most critical issue and show how you'd fix it
// 3. What would you recommend for a real codebase?

suspend fun processWithLock(entityId: String, block: suspend () -> Unit) {
  val lockKey = "lock:$entityId"
  val acquired = redis.setnx(lockKey, "locked")  // setnx = SET if Not eXists
  if (!acquired) return

  try {
    block()
  } finally {
    redis.del(lockKey)
  }
}
