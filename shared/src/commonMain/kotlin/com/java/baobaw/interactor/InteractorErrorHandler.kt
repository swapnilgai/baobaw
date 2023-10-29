package com.java.baobaw.interactor

import dev.icerock.moko.resources.StringResource
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

abstract class InteractorErrorHandler : AbstractCoroutineContextElement(InteractorErrorHandler){
    companion object Key: CoroutineContext.Key<InteractorErrorHandler>

    abstract fun awaitRetryOptionOrNull(error: Throwable): AwaitRetryOptions?

    abstract suspend fun awaitRetry(options: AwaitRetryOptions)
}

data class AwaitRetryOptions(
    val title: StringResource?,
    val message: StringResource,
    val description: StringResource
)