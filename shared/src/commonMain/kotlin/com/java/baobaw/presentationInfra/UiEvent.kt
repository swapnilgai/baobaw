package com.java.baobaw.presentationInfra

import dev.icerock.moko.resources.StringResource

sealed class UiEvent<ContentT>{
    data class Content<ContentT>(
        val value: ContentT
    ) : UiEvent<ContentT>()
    object Loading : UiEvent<Nothing>()
    data class Error( val title: StringResource,
                      val message: StringResource): UiEvent<Nothing>()
    data class Navigation(val route: String, val navigateToScreen: Boolean = false): UiEvent<Nothing>()

    object Cancled : UiEvent<Nothing>()

    object CustomLoading : UiEvent<Nothing>()
}