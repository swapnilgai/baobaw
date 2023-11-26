package com.java.baobaw.feature.chat

import com.java.baobaw.AppConstants
import io.github.jan.supabase.realtime.PostgresAction


import com.java.baobaw.presentationInfra.BaseViewModel
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.util.getNavigationUrlWithoutBrackets
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ChatListContent(
    val messages: List<LastMessage>,
    val currentPageNumber: Long = 1,
    val isLastPage: Boolean = false,
    val isLoading: Boolean = false
)

class ChatListViewModel(
    private val chatListInteractor: ChatListInteractor,
    private val chatRealtimeInteractor: ChatRealtimeInteractor
) : BaseViewModel<ChatListContent>(ChatListContent(emptyList())) {

    fun init() {
        loadMoreMessages(1)
        subscribeToNewMessages()
    }
    fun loadMoreMessages(currentPageNumber: Long = getContent().currentPageNumber) {
        val currentState = getContent()

        // Guard against multiple simultaneous loads and if it's the last page
      //  if (currentState.isLoading || currentState.isLastPage) return

        setContent { currentState.copy(isLoading = true) }

        setLoading()
        viewModelScope.interactorLaunch {
                val result = chatListInteractor.getLastMessages(currentPageNumber, 20L)
                val newPageNumber = currentState.currentPageNumber + 1
                val newIsLastPage = newPageNumber >= result.totalPages

                setContent {
                    ChatListContent(
                        messages = result.data,
                        currentPageNumber = newPageNumber,
                        isLastPage = newIsLastPage,
                        isLoading = false
                    )
                }
        }
    }

    fun subscribeToNewMessages() {
        viewModelScope.interactorLaunch {
            chatRealtimeInteractor.subscribeToNewMessages()
                .onEach { newMessage ->
                    val combinedList = chatListInteractor.updateMessagesWithNewData(getContent().messages, listOf(newMessage))
                    setContent {
                        getContent().copy(messages = combinedList)
                    }
                }.launchIn(this)
            chatRealtimeInteractor.subscribe()
        }
    }
    override suspend fun clearViewModel() {
        viewModelScope.launch {
            chatRealtimeInteractor.unSubscribe()
        }.join()
    }

    fun navigateToChatDetail(referenceId: String) {
        navigate(getNavigationUrlWithoutBrackets(AppConstants.RoutIds.CHAT_DETAIL_SCREEN, listOf(referenceId)))
    }
}
