package com.java.baobaw.feature.chat

import com.java.baobaw.AppConstants


import com.java.baobaw.presentationInfra.BaseViewModel
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.util.getNavigationUrlWithoutBrackets
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class ChatListContent(
    val messages: List<LastMessage>,
    val isLoading: Boolean = false,
    val offset: Long = 0L,
    val totalRecords: Long = 1L
)

class ChatListViewModel(
    private val chatListInteractor: ChatListInteractor
) : BaseViewModel<ChatListContent>(ChatListContent(emptyList())) {

    fun init() {
        viewModelScope.interactorLaunch {
           async { loadMoreMessages() }
           async { subscribeToNewMessages() }
        }
    }
    fun loadMoreMessages() {
        val currentState = getContent()

        // Guard against multiple simultaneous loads and if it's the last page
        if (currentState.isLoading ) {
            return
        }
        setContent { currentState.copy(isLoading = true) }
        val newOffset = if(currentState.totalRecords > currentState.offset) currentState.offset + 20L
        else currentState.offset
        setLoading()
        viewModelScope.interactorLaunch {
            val isLastPage = getContent().totalRecords <= getContent().messages.size
            val result = chatListInteractor.getLastMessages(newOffset, isLastPage  = isLastPage, getContent().messages)

            setContent {
                ChatListContent(
                    messages = result.data,
                    isLoading = false,
                    offset = result.offset,
                    totalRecords = result.totalRecords
                )
            }
        }
    }

    fun subscribeToNewMessages() {
        // Subscribe to conversation logic
        viewModelScope.interactorLaunch {
            chatListInteractor.getLastMessagesFlow()
                .onEach { result ->
                    setContent {
                        getContent().copy(messages = result.data)
                    }
                }.launchIn(this)
        }
    }

    fun navigateToChatDetail(referenceId: String) {
        navigate(getNavigationUrlWithoutBrackets(AppConstants.RoutIds.CHAT_DETAIL_SCREEN, listOf(referenceId)))
    }
}
