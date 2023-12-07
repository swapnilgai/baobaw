package com.java.baobaw.interactor

import com.java.baobaw.BuildKonfig
import com.java.baobaw.cache.Cache
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

val cache : Cache<CacheKey, Any> = LRUCache(1000)
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
        val cacheResult: T? = if (isCacheAllowed && cacheOption != null && !cacheOption.skipCache) {
            cache.get(cacheOption.key) as T?
        } else {
            null
        }
        return@withContext if (cacheResult != null) {
            cacheResult
        } else {
            var attemptIndex = -1
            var blockResult: T
            while (true) {
                attemptIndex++
                try {
                    // await any existing retry deferred
                    retryRequestDeferred?.await()

                    // Apply any delay defined by 'retryOptions'
                    if (attemptIndex > 0) {
                        val dealy = retryOption.delayForRetryAttempt(attemptIndex)
                        delay(dealy)
                    }

                    // Run the block in a new coroutineScope to correctly handle exceptions thrown by coroutines started within 'block'
                    // which would otherwise cancel the outer scope causing the catch block below to receive a JobCancellationException
                    // in place of the actual exception that occurred.
                    blockResult = coroutineScope {
                        if (attemptIndex > 0 && retryOption.forceRefreshDuringRetry) {
                            withContext(CacheCoroutineContextElement(setCache = false)) { block() }
                        } else {
                            block()
                        }
                    }

                    // Check if we should retry based on the given 'retryOptions' and the result of the block
                    if (
                        attemptIndex < retryOption.retryCount
                        && retryOption.retryCondition(Result.success(blockResult))
                        ) {
                        continue
                    }
                    cacheOption?.run {
                        // If writing to the cache is allowed, store the result
                        if(allowWrite){
                            cache.put(key, blockResult as Any)
                        }
                    }
                    break
                } catch (e: Exception) {
                    // Check if we should await a retry based on the 'interactorErrorHandler'
                    val awaitRetryOptions = interactorErrorHandling?.awaitRetryOptionOrNull(e)
                    if (awaitRetryOptions != null){
                        if(retryRequestDeferred == null) {
                            retryRequestDeferred = async { interactorErrorHandling.awaitRetry(awaitRetryOptions) }
                            retryRequestDeferred?.await()
                            retryRequestDeferred = null
                        }
                        continue
                    }

                    // Check if we should retry based on the given 'retryOptions' and the exception thrown by the block
                    if (attemptIndex < retryOption.retryCount && retryOption.retryCondition(Result.failure(e))) {
                        continue
                    }
                    // we have determined that we should not attempt to retry: throw an exception
                    if(retryOption.objectToReturn != null)
                        return@withContext retryOption.objectToReturn

                    if (retryOption.throwException) {
                        throw when {
                            !isFirstInteractorCall -> e // nested withInteractorContext call: throw the raw exception
                            e is HttpRequestException || e is RestException -> e.toInteractorException()
                            else -> e.toInteractorException()
                        }
                    }
                }
            }
            blockResult
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
        attemptNumber == 1 -> initialDelay
        else -> (initialDelay * delayIncrementalFactor * (attemptNumber -1)).toLong()
    }
        .coerceAtLeast(0L)
        .coerceAtMost(maxDelay)
}

suspend fun Interactor.invalidateCache(cacheKey: CacheKey) {
    if(coroutineContext[InteractorCoroutineContextElement] == null){
        if(BuildKonfig.environment == "debug"){
            throw IllegalStateException("invalidateCache should be called from within an interactor")
        }
    }
    cache.remove(cacheKey)
}