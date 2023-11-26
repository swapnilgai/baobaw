package com.java.baobaw.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface CacheKey
interface Cache<K, V> {
    suspend fun get(key: K): V?
    suspend fun put(key: K, value: V)
}

class LRUCache<K, V>(private val capacity: Int) : com.java.baobaw.cache.Cache<K, V> {
    private val cacheMap = LinkedHashMap<K, V>(capacity, 0.75f)
    private val lock = Mutex()

    override suspend fun get(key: K): V? = lock.withLock {
        return cacheMap[key]
    }

    override suspend fun put(key: K, value: V) = lock.withLock {
        if (cacheMap.size >= capacity) {
            cacheMap.remove(cacheMap.keys.first())
        }
        cacheMap[key] = value
    }
    fun remove(key: K) {
        cacheMap.remove(key)
    }

    fun clear() {
        cacheMap.clear()
    }
}

