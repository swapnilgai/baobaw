package com.java.cherrypick.interactor

import com.java.cherrypick.cache.CacheKey
import com.java.cherrypick.cache.LRUCache
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

private val cache = LRUCache<CacheKey, Any>(100)
suspend fun <T> Interactor.withInteractorContext(
    cacheOption: CacheOption ?= null,
    retryOption: RetryOption<T> = RetryOption(0),
    block: suspend CoroutineScope.() -> T
) : T {

    val isCachellowed = coroutineContext[CacheCoroutineContextElement]?.setCache == true
    val interactorErrorHandling = coroutineContext[InteractorErrorHandler]
    val isFirstInteractorCall = coroutineContext[InteractorCoroutineContextElement] == null
    val context = InteractorDispatcherProvider.dispatcher + InteractorCoroutineContextElement(true)

    return withContext(context) {
        val cacheResult : T? = if (isCachellowed && cacheOption != null) {
            cache.get(cacheOption.key) as T?
        } else {
            null
        }
        return@withContext if (cacheResult != null) {
            cacheResult
        } else {
            var attemptIndex = -1
            var blockResult: T
            loop@ while (true) {
                attemptIndex++
                try {
                    retryRequestDeferred?.await()
                    if (attemptIndex > 0) {
                        delay(retryOption.delayForRetryAttempt(attemptIndex))
                    }


                    blockResult = coroutineScope {
                        if(attemptIndex > 0 && retryOption.forceRefreshDuringRetry){
                            withContext(CacheCoroutineContextElement(setCache = false)){
                                    block()
                            }
                        } else {
                            block()
                        }
                    }
                    // check if we should retry based on the given 'retryCondition' and the result of the block
                    if (attemptIndex < retryOption.retryCount && retryOption.retryCondition(
                            Result.success(
                                blockResult
                            )
                        )
                    ) {
                        continue
                    }
                    cacheOption?.run {
                        if(allowWrite){
                            cache.put(key, blockResult as Any)
                        }
                    }

                    break
                } catch (e: Exception) {
                    // check if we should await a retry based on the 'interactorErrorHandling'
                    val awaitRetryOptions = interactorErrorHandling?.awaitRetryOptionOrNull(e)
                    if (awaitRetryOptions != null) {
                        if (retryRequestDeferred == null) { //reuse existing deferred if available
                            retryRequestDeferred =
                                async { interactorErrorHandling.awaitRetry(awaitRetryOptions) }
                            retryRequestDeferred?.await()
                            retryRequestDeferred = null
                        }
                        continue
                    }

                    // check if we should retry based on the given 'retryCondition' and the exception thrown by the block
                    if (retryOption.retryCondition(Result.failure(e)) && attemptIndex < retryOption.retryCount) {
                        continue
                    }
                    // we have determined we should not attempt to retry: throw an exception
                    throw when {
                        !isFirstInteractorCall -> e // nested withInteractorContext call: throw the raw exception
                        e is HttpRequestException || e is RestException -> e.toInteractorException()
                        else -> e.toInteractorException()
                    }
                }
            }
            return@withContext blockResult
        }
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