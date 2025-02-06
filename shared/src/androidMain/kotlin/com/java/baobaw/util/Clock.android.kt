/**
 * Expect class for platform-specific time retrieval
 */
actual object Clock {
    actual fun currentMillis(): Long = System.currentTimeMillis()

}
