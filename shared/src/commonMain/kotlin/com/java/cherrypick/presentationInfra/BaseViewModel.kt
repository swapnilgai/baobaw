package com.java.cherrypick.presentationInfra

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class BaseViewModel<ContentT>(contentT : ContentT) {

    //TODO find common folder for strings.xml
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, exception ->
        setError(error = exception.message?: "")
    }
    val viewModelScope = CoroutineScope( SupervisorJob() + Dispatchers.Main + coroutineExceptionHandler )
    val uiChannel: UiChannel<ContentT> = UiChannelImpl<ContentT>(initialContent = contentT)

    fun setContent(contentT: ContentT){
        uiChannel.setContent(contentT)
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