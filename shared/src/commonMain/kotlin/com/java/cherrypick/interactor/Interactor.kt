package com.java.cherrypick.interactor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

interface Interactor

suspend fun <T> Interactor.withInteractorContext(
    block: suspend CoroutineScope.() -> T
) : T? {
    val context = InteractorDispatcherProvider.dispatcher

    return withContext(context) {
            var blockResult : T ? = null
            try {
                blockResult = coroutineScope {
                    block()
                }
            } catch (e: Exception){
                throw e.toInteractorException()
            }
            return@withContext blockResult
        }
}
