package com.java.baobaw.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface CacheKey
interface Cache<K, V> {
    fun get(key: K): V?
    fun put(key: K, value: V)
    fun remove(key: K)
}

class LRUCache<K, V>(private val capacity: Int) : Cache<K, V> {
    private val cacheMap = LinkedHashMap<K, V>(capacity, 0.75f)
    private val lock = Mutex()

    override fun get(key: K): V?  {
        return cacheMap[key]
    }

    override fun put(key: K, value: V) {
        if (cacheMap.size >= capacity) {
            cacheMap.remove(cacheMap.keys.first())
        }
        cacheMap[key] = value
    }
    override fun remove(key: K) {
        cacheMap.remove(key)
    }
}

