package com.java.baobaw.feature.chat

import com.java.baobaw.AppConstants


import com.java.baobaw.presentationInfra.BaseViewModel
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.util.getNavigationUrlWithoutBrackets
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class ChatListContent(
    val messages: List<LastMessage>,
    val isLoading: Boolean = false,
    val offset: Long = 0L,
    val totalRecords: Long = 1L
)

class ChatListViewModel(
    private val chatListInteractor: ChatListInteractor,
    private val chatRealtimeInteractor: ChatRealtimeInteractor
) : BaseViewModel<ChatListContent>(ChatListContent(emptyList())) {

    fun init() {
        loadMoreMessages()
        subscribeToNewMessages()
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
        viewModelScope.interactorLaunch {
            chatRealtimeInteractor.subscribeToLastMessages(chatListType = ChatListType.LIST_MESSAGES)
                .onEach { newMessage ->
                    val combinedList = chatListInteractor.updateMessages(newMessage)
                    setContent {
                        getContent().copy(messages = combinedList.data)
                    }
                }.launchIn(this)
        }
    }
    override suspend fun clearViewModel() {
        viewModelScope.interactorLaunch {
            chatRealtimeInteractor.unSubscribe(chatListType = ChatListType.LIST_MESSAGES)
        }.join()
    }

    fun navigateToChatDetail(referenceId: String) {
        navigate(getNavigationUrlWithoutBrackets(AppConstants.RoutIds.CHAT_DETAIL_SCREEN, listOf(referenceId)))
    }
}
