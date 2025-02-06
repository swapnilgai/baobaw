package com.java.baobaw.cache

interface CacheKey

data class StringCacheKey(val stringKey: String) : CacheKey

class LRUCache private constructor(private val maxSize: Int) {
    private val cache: MutableMap<CacheKey, Any> = LinkedHashMap()

    @Suppress("UNCHECKED_CAST")
    fun set(
        key: CacheKey,
        secondaryKey: CacheKey? = null,
        expirationPolicy: CacheExpirationPolicy,
        requestTimestamp: Long,
        value: Any?
    ) {
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

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: CacheKey, secondaryKey: CacheKey? = null): T? {
        val entry = if (secondaryKey == null) {
            cache[key] as? CacheEntry
        } else {
            (cache[key] as? MutableMap<CacheKey, CacheEntry>)?.get(secondaryKey)
        }
        return if (entry?.isExpired == false) entry.data as T else null
    }

    fun remove(key: CacheKey): Any? = cache.remove(key)

    fun clear() = cache.clear()

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
