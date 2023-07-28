package com.java.cherrypick.interactor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

interface Interactor

private var retryRequestDeferred: Deferred<Unit>? = null
suspend fun <T>Interactor.withInteractorContext(
    block: suspend CoroutineScope.() -> T,
    retryOption: RetryOption = RetryOption(0)
) : T? {

    val context = InteractorDispatcherProvider.dispatcher
    val interactorErrorHandling = coroutineContext[InteractorErrorHandler]
    val isFirstInteractorCall = coroutineContext[InteractorCoroutineContextElement] == null

    return withContext(context) {
        var attemptIndex = -1
        var blockResult: T

        loop@ while (true) {
            attemptIndex++
            try {
                retryRequestDeferred?.await()
                if(attemptIndex>0){
                    delay(retryOption.delayForRetryAttempt())
                }
                blockResult = coroutineScope {
                    block()
                }


            } catch (e: Exception) {
                throw e.toInteractorException()
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

private fun RetryOption.delayForRetryAttempt(attemptNumber: Int): Long{
    return when{
        attemptNumber <= 0 -> 0L
        attemptNumber ==1 -> initialDelay
        else -> (initialDelay + delayIncrementalFactor + (attemptNumber -1)).toLong()
    }
        .coerceAtLeast(0L)
        .coerceAtMost(maxDelay)
}