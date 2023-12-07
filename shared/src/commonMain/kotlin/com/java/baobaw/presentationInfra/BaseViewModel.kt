package com.java.baobaw.presentationInfra

import com.java.baobaw.SharedRes
import com.java.baobaw.executor.MainDispatcher
import com.java.baobaw.interactor.AwaitRetryOptions
import com.java.baobaw.interactor.InteracroeException
import com.java.baobaw.interactor.InteractorErrorHandler
import dev.icerock.moko.resources.StringResource
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel<ContentT>(initialContent : ContentT): KoinComponent {

    private val mainDispatcher: MainDispatcher by inject()
    private var currentState: ContentT = initialContent

    //TODO find common folder for strings.xml
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, exception ->
        when(exception){
            is InteracroeException -> setError(message = exception.messageRes)
        }
    }

    private val interactorErrorHandler = object : InteractorErrorHandler(){

        override fun awaitRetryOptionOrNull(error: Throwable): AwaitRetryOptions? {
            return this@BaseViewModel.awaitRetryOptionOrNull(error)
        }

        override suspend fun awaitRetry(options: AwaitRetryOptions) {
            val retryTrigger = Channel<Unit>(capacity = 1)
            withContext(mainDispatcher.dispatcher){
                //setError()
            }
            val awaitRetryScope = CoroutineScope(Job())
            awaitRetryScope.launch {
                retryTrigger.trySend(Unit).isSuccess
            }
            retryTrigger.receive()
            awaitRetryScope.cancel()
            withContext(mainDispatcher.dispatcher){ setLoading() }
        }
    }

    var viewModelScope: CoroutineScope = CoroutineScope( SupervisorJob() + mainDispatcher.dispatcher + coroutineExceptionHandler + interactorErrorHandler)

    private val _state = MutableStateFlow<UiEvent<out ContentT>>(UiEvent.Content(initialContent))

    val state = _state.asStateFlow()
    //stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), initialContent)

    protected fun setContent(reduce: ContentT.() -> ContentT) {
        currentState = currentState.reduce()
        UiEvent.Content(currentState).let {
                newValue ->  if(_state.value != newValue) _state.tryEmit(UiEvent.Content(currentState))
        }
    }

    fun getContent() = currentState

    fun setLoading(){
        if(_state.value != UiEvent.Loading)
        _state.value = UiEvent.Loading
    }

    fun setCustomLoading(){
        if(_state.value != UiEvent.CustomLoading)
        _state.value = UiEvent.CustomLoading
    }
    fun clear(){
        _state.tryEmit(UiEvent.Cancled)
        viewModelScope.cancel()
    }

    open suspend fun clearViewModel(){}
    fun onStart(){
        if(!viewModelScope.isActive)
            viewModelScope = CoroutineScope( SupervisorJob() + mainDispatcher.dispatcher + coroutineExceptionHandler )
    }
    fun navigate(route: String){
        UiEvent.Navigation(route).let {
                newValue ->  if(_state.value != newValue) _state.tryEmit(newValue)
        }
    }

    fun setError(title: StringResource = SharedRes.strings.error, message: StringResource){
        UiEvent.Error(title = title, message = message).let {
            _state.tryEmit(it)
        }
    }
    fun onDismiss(){
        setContent {
            getContent()
        }
    }

    /**
     *   Return [AwaitRetryOptions] if an error UI with retry option should be displayed
     *   for the give interactor [error]. Retrun null otherwise
      */
    protected open fun awaitRetryOptionOrNull(
        error: Throwable
    ): AwaitRetryOptions?{
        return when (error) {
            is IOException -> {   // check if network is avaialble
                AwaitRetryOptions(
                    title = SharedRes.strings.error,
                    message = SharedRes.strings.error,
                    description = SharedRes.strings.error
                )
            }
            else -> null
        }
    }
}