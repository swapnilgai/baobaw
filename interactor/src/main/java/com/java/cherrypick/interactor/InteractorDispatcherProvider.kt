package com.java.cherrypick.interactor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object InteractorDispatcherProvider {
    internal val defaultDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    internal val dispatcher : CoroutineDispatcher = Dispatchers.Unconfined
}