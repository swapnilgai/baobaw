package com.java.cherrypick.interactor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

val allowCache = CacheCoroutineContextElement(setCache = true)

fun CoroutineScope.interactorLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
   start: CoroutineStart = CoroutineStart.DEFAULT,
   forceRefresh: Boolean = false,
   block: suspend CoroutineScope.() -> Unit
) : Job {
    return if(forceRefresh)
        launch(context, start, block)
    else {
       val newContext =  allowCache + this.coroutineContext + context
       launch(newContext, start, block)
    }
}