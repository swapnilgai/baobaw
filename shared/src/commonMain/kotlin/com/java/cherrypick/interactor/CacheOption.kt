package com.java.cherrypick.interactor

import com.java.cherrypick.cache.CacheKey

data class CacheOption( val key: CacheKey,
    val allowWrite: Boolean = true )

