package com.java.cherrypick.presentationInfra

import com.java.cherrypick.executor.MainDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel1<State>(state : State): KoinComponent {

    private val mainDispatcher: MainDispatcher by inject()

    abstract var error: (String) -> Unit

    abstract var loading: () -> Unit
    private var currentState: State
        get() = if(state.value is UiEvent.Content<out State>) (state.value as UiEvent.Content<out State>).value else currentState

    init {
        currentState = state
    }


    //TODO find common folder for strings.xml
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, exception ->
        error.invoke(exception.message?: "")
    }
    val viewModelScope = CoroutineScope( SupervisorJob() + mainDispatcher.dispatcher + coroutineExceptionHandler )

    private val _state = MutableStateFlow<UiEvent<out State>>(UiEvent.Content(state))
    val state = _state.asStateFlow()

    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _state.value = UiEvent.Content(newState)
    }

    fun getContent() = currentState

    fun clear(){
        viewModelScope.cancel()
    }

    fun navigate(route: String){
        _state.value = UiEvent.Error("Test error message")
    }
}