package com.java.baobaw.interactor

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class CacheCoroutineContextElement(val setCache: Boolean = true): AbstractCoroutineContextElement(CacheCoroutineContextElement) {
        companion object Key: CoroutineContext.Key<CacheCoroutineContextElement>
}
