package com.java.cherrypick.interactor

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

abstract class InteractorErrorHandler : AbstractCoroutineContextElement(InteractorErrorHandler){
    companion object Key: CoroutineContext.Key<InteractorErrorHandler>

    abstract fun awaitRetryOptionOrNull(error: Throwable): AwaitRetryOptions?

    abstract suspend fun awaitRetry(options: AwaitRetryOptions)
}

data class AwaitRetryOptions(
    val title: String?,
    val message: String,
    val description: String
)