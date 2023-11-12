package com.java.baobaw.interactor

import com.java.baobaw.cache.CacheKey
import com.java.baobaw.cache.LRUCache
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

interface Interactor

private var retryRequestDeferred: Deferred<Unit>? = null

private val cache =
    LRUCache<CacheKey, Any>(100)
suspend fun <T> Interactor.withInteractorContext(
    cacheOption: CacheOption? = null,
    retryOption: RetryOption<T> = RetryOption(0),
    block: suspend CoroutineScope.() -> T
): T {
    val isCacheAllowed = coroutineContext[CacheCoroutineContextElement]?.setCache == true
    val interactorErrorHandling = coroutineContext[InteractorErrorHandler]
    val isFirstInteractorCall = coroutineContext[InteractorCoroutineContextElement] == null
    val context = InteractorDispatcherProvider.dispatcher + InteractorCoroutineContextElement(true)

    return withContext(context) {
        cacheOption?.takeIf { isCacheAllowed }?.key?.let { key ->
            return@withContext cache.get(key) as T
        }
        var attemptIndex = 0
        var blockResult: T
        while (true) {
            try {
                retryRequestDeferred?.await()
                retryRequestDeferred = null

                if (attemptIndex > 0) delay(retryOption.delayForRetryAttempt(attemptIndex))

                blockResult = coroutineScope {
                    if (attemptIndex > 0 && retryOption.forceRefreshDuringRetry) {
                        withContext(CacheCoroutineContextElement(setCache = false)) { block() }
                    } else {
                        block()
                    }
                }

                if (attemptIndex >= retryOption.retryCount || !retryOption.retryCondition(Result.success(blockResult))) {
                    break
                }
            } catch (e: Exception) {
                if (attemptIndex >= retryOption.retryCount || !retryOption.retryCondition(Result.failure(e))) {
                    val awaitRetryOptions = interactorErrorHandling?.awaitRetryOptionOrNull(e)
                    if (awaitRetryOptions != null && retryRequestDeferred == null) {
                        retryRequestDeferred = async { interactorErrorHandling.awaitRetry(awaitRetryOptions) }
                        continue
                    }

                    if (retryOption.throwException) {
                        throw when {
                            !isFirstInteractorCall -> e // nested withInteractorContext call: throw the raw exception
                            e is HttpRequestException || e is RestException -> e.toInteractorException()
                            else -> e.toInteractorException()
                        }
                    } else throw IllegalStateException("State is not valid") //TODO check alternative to break loop
                }
            }
            attemptIndex++
        }
        cacheOption?.takeIf { it.allowWrite }?.let { cache.put(it.key, blockResult as Any) }
        blockResult
    }
}

private data class InteractorCoroutineContextElement(
    val isInteractor: Boolean
): AbstractCoroutineContextElement(InteractorCoroutineContextElement){
    companion object Key: CoroutineContext.Key<InteractorCoroutineContextElement>
}

private fun <T> RetryOption<T>.delayForRetryAttempt(attemptNumber: Int): Long{
    return when{
        attemptNumber <= 0 -> 0L
        attemptNumber ==1 -> initialDelay
        else -> (initialDelay + delayIncrementalFactor + (attemptNumber -1)).toLong()
    }
        .coerceAtLeast(0L)
        .coerceAtMost(maxDelay)
}