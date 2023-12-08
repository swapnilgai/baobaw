package com.java.baobaw.interactor

import com.java.baobaw.cache.CacheKey

data class CacheOption(
    val key: CacheKey,
    val allowWrite: Boolean = true,
    val skipCache: Boolean = false )

