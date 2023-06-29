package com.java.cherrypick.presentationInfra

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseViewModel<ContentT>(contentT : ContentT) {

    val viewModelScope = CoroutineScope( SupervisorJob() + Dispatchers.Main )
    val uiChannel = UiChannelImpl<ContentT>(initialContent = contentT)

    fun setContent(contentT: ContentT){
        uiChannel.setContent(contentT)
    }

    fun setError(error: String){
        uiChannel.showDialog(error)
    }

    fun setLoading(){
        uiChannel.setLoading()
    }

}