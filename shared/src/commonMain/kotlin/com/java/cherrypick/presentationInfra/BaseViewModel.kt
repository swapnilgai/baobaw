package com.java.cherrypick.presentationInfra

import com.java.cherrypick.executor.MainDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel<ContentT>(initialContent : ContentT): KoinComponent {

    private val mainDispatcher: MainDispatcher by inject()
    private var currentState: ContentT = initialContent

    //TODO find common folder for strings.xml
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, exception ->
        setError(exception.message?: "")
    }

    var viewModelScope: CoroutineScope = CoroutineScope( SupervisorJob() + mainDispatcher.dispatcher + coroutineExceptionHandler )

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
    fun clear(){
        if(_state.value!=UiEvent.Nothing) {
            viewModelScope.cancel()
            _state.tryEmit(UiEvent.Nothing)
        }
    }

    fun onStart(){
        if(!viewModelScope.isActive) viewModelScope = CoroutineScope( SupervisorJob() + mainDispatcher.dispatcher + coroutineExceptionHandler )
    }
    fun navigate(route: String){
        UiEvent.Navigation(route).let {
                newValue ->  if(_state.value != newValue) _state.tryEmit(UiEvent.Navigation(route))
        }
    }
    fun setError(error: String){
            UiEvent.Error(error).let {
                _state.tryEmit(UiEvent.Error(error))
            }
    }
}