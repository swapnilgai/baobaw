package com.java.baobaw.cache

/**
 * Expect class for platform-specific time retrieval
 */
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual object Clock {
    actual fun currentMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
}