package com.java.cherrypick.presentationInfra

sealed class UiEvent<ContentT>{
    data class Content<ContentT>(
        val value: ContentT
    ) : UiEvent<ContentT>()
    object Loading : UiEvent<Nothing>()
    data class Error(val message: String): UiEvent<Nothing>()
}

