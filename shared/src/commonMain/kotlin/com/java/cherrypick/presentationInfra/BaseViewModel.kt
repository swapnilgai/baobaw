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

abstract class BaseViewModel<State>(initialState : State): KoinComponent {

    private val mainDispatcher: MainDispatcher by inject()
    private var currentState: State = initialState

    //TODO find common folder for strings.xml
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, exception ->
        setError(exception.message?: "")
    }
    val viewModelScope = CoroutineScope( SupervisorJob() + mainDispatcher.dispatcher + coroutineExceptionHandler )

    private val _state = MutableStateFlow<UiEvent<out State>>(UiEvent.Content(initialState))
    val state = _state.asStateFlow()

    protected fun setState(reduce: State.() -> State) {
        currentState = currentState.reduce()
        _state.value = UiEvent.Content(currentState)
    }

    fun getContent() = currentState

    fun setLoading(){
        _state.value = UiEvent.Loading
    }
    fun clear(){
        viewModelScope.cancel()
    }

    fun navigate(route: String){
        _state.value = UiEvent.Error("Navigation")
    }
    fun setError(error: String){
        _state.value = UiEvent.Error(error)
    }
}