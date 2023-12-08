package com.java.baobaw.cache

import kotlinx.coroutines.sync.Mutex
import kotlin.jvm.Synchronized

interface CacheKey
interface Cache<K, V> {
    fun get(key: K): V?
    fun put(key: K, value: V)
    fun remove(key: K)
    fun clearAll()
}
class LRUCache<K, V>(private val capacity: Int) : Cache<K, V> {
    private val cacheMap = LinkedHashMap<K, V>(capacity, 0.75f)
    @Synchronized
    override fun get(key: K): V?  {
        return cacheMap[key]
    }
    @Synchronized
    override fun put(key: K, value: V) {
        if (cacheMap.size >= capacity) {
            cacheMap.remove(cacheMap.keys.first())
        }
        cacheMap[key] = value
    }
    @Synchronized
    override fun remove(key: K) {
        cacheMap.remove(key)
    }

    @Synchronized
    override fun clearAll() {
        cacheMap.clear()
    }
}

