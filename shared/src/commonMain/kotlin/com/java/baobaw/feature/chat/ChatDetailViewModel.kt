package com.java.baobaw.feature.chat

import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: Long,
    val referenceId: String,
    val creatorUserId: String,
    val message: String?, // Nullable since "message" can be null
    val createdTime: String,
    val createdDate: String,
    val seen: Boolean,
    val isDeleted: Boolean,
    val isUserCreated: Boolean = false,
    val isHeader: Boolean = false, // Transient property, defaulting to false
    val messageId: Long,
    val sent: Boolean = true
)

class ChatDetailViewModel(private val chatDetailInteractor: ChatDetailInteractor): BaseViewModel<PagedResponse>(initialContent =  PagedResponse()) {
    private var isLoading = false

    fun init(referenceId: String){
        viewModelScope.interactorLaunch {
            chatDetailInteractor.setReferenceId(referenceId)
            async {  getConversation(isInitial = true) }
            async {  subscribeToConversation(referenceId) }
        }
    }

    fun sendMessage(inputText: String, referenceId: String) {
        viewModelScope.interactorLaunch {
            val request = chatDetailInteractor.getChatMessageRequest(inputText, referenceId)
            val newResult =  async { chatDetailInteractor.addTempMessage(request, referenceId) }
            val result = newResult.await()
            setContent { result }
            async { chatDetailInteractor.sendMessage(request) }
        }
    }

    // The function to get the conversation as described.
    fun getConversation(isInitial: Boolean = false) {
        if(isLoading || getContent().isEnded) return
        setCustomLoading()
        viewModelScope.interactorLaunch {
            isLoading = true
            val result = chatDetailInteractor.getNextPage(isInitial, getContent())
            setContent { result }
            isLoading = false
        }
    }

    fun subscribeToConversation(referenceId: String) {
        // Subscribe to conversation logic
        viewModelScope.interactorLaunch {
            chatDetailInteractor.getMessagesFlow(referenceId)
                    .onEach { result ->
                       if(result.isNotEmpty()){
                           setContent { getContent().copy(data = result) }
                       }
                    }.launchIn(this)
            }
    }

    override suspend fun clearViewModel() {
        setContent { PagedResponse() }
        isLoading = false
    }
}