package com.java.cherrypick.presentationInfra

import com.java.cherrypick.executor.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel<State>(state : State): KoinComponent {

    private val mainDispatcher: MainDispatcher by inject()

    abstract var error: (String) -> Unit

    abstract var loading: () -> Unit
    private val currentState: State
        get() = state.value

    //TODO find common folder for strings.xml
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, exception ->
        error.invoke(exception.message?: "")
    }
    val viewModelScope = CoroutineScope( SupervisorJob() + mainDispatcher.dispatcher + coroutineExceptionHandler )

    private val _state = MutableStateFlow(state)
    val state = _state.asStateFlow()

    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _state.value = newState
    }

    fun getContent() = currentState

    fun clear(){
        viewModelScope.cancel()
    }
}