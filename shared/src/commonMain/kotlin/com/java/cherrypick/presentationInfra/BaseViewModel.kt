package com.java.cherrypick.presentationInfra

import com.java.cherrypick.executor.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel<State>(state : State): KoinComponent {

    private val mainDispatcher: MainDispatcher by inject()

    //TODO find common folder for strings.xml
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, exception ->
        setError(error = exception.message?: "")
    }
    val viewModelScope = CoroutineScope( SupervisorJob() + mainDispatcher.dispatcher + coroutineExceptionHandler )
    val uiChannel: UiChannel<State> = UiChannelImpl<State>(initialState = state)

    fun setContent(state: State){
        uiChannel.setContent(state)
    }

    fun getContent() = uiChannel.getContent()

    fun setError(error: String){
        uiChannel.showDialog(error)
    }

    fun setLoading(){
        uiChannel.setLoading()
    }
    fun clear(){
        viewModelScope.cancel()
    }
}