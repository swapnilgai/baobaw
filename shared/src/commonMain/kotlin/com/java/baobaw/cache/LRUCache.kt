package com.java.baobaw.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface CacheKey

data class StringCacheKey(val stringKey: String) : CacheKey

class LRUCache private constructor(private val maxSize: Int) {
    private val mutex = Mutex()
    private val cache: MutableMap<CacheKey, Any> = LinkedHashMap()

    suspend fun set(
        key: CacheKey,
        secondaryKey: CacheKey? = null,
        expirationPolicy: CacheExpirationPolicy,
        requestTimestamp: Long,
        value: Any?
    ) = mutex.withLock {
        if (secondaryKey == null) {
            if (value == null) {
                cache.remove(key)
            } else {
                enforceSizeLimit()
                cache[key] = CacheEntry(value, requestTimestamp, expirationPolicy)
            }
        } else {
            var secondaryMap = cache[key] as? MutableMap<CacheKey, CacheEntry>
            if (secondaryMap == null) {
                secondaryMap = mutableMapOf()
                cache[key] = secondaryMap
            }

            if (value == null) {
                secondaryMap.remove(secondaryKey)
            } else {
                enforceSizeLimit()
                secondaryMap[secondaryKey] = CacheEntry(value, requestTimestamp, expirationPolicy)
            }
        }
    }

    suspend fun <T> get(key: CacheKey, secondaryKey: CacheKey? = null): T? = mutex.withLock {
        val entry = if (secondaryKey == null) {
            cache[key] as? CacheEntry
        } else {
            (cache[key] as? MutableMap<CacheKey, CacheEntry>)?.get(secondaryKey)
        }
        return@withLock if (entry?.isExpired == false) entry.data as T else null
    }

    suspend fun remove(key: CacheKey): Any? = mutex.withLock {
        cache.remove(key)
    }

    suspend fun clear() = mutex.withLock {
        cache.clear()
    }

    private fun enforceSizeLimit() {
        if (cache.size >= maxSize) {
            val eldestKey = cache.keys.firstOrNull()
            if (eldestKey != null) cache.remove(eldestKey)
        }
    }

    private data class CacheEntry(
        val data: Any,
        val requestTimestamp: Long,
        val expirationPolicy: CacheExpirationPolicy
    ) {
        val isExpired: Boolean
            get() = expirationPolicy.isEntryExpired(requestTimestamp)
    }

    companion object {
        fun create(maxSize: Int): LRUCache = LRUCache(maxSize)
    }
}
