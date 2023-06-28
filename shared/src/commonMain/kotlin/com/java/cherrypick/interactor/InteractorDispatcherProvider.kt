package com.java.cherrypick.interactor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object InteractorDispatcherProvider {
    internal val dispatcher : CoroutineDispatcher = Dispatchers.Unconfined
}