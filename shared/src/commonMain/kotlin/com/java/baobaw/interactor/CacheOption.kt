package com.java.baobaw.interactor

import com.java.baobaw.cache.CacheKey

data class CacheOption(val key: com.java.baobaw.cache.CacheKey,
                       val allowWrite: Boolean = true )

