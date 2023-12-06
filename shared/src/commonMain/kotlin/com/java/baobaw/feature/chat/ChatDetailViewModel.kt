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

class ChatDetailViewModel(private val chatDetailInteractor: ChatDetailInteractor): BaseViewModel<Map<String, List<ChatMessage>>>(initialContent =  emptyMap()) {

    fun init(referenceId: String){
        viewModelScope.interactorLaunch {
            chatDetailInteractor.setReferenceId(referenceId)
            async {  getConversation(referenceId) }
            async {  subscribeToConversation(referenceId) }
        }
    }

    fun sendMessage(inputText: String, referenceId: String) {
        viewModelScope.interactorLaunch {
            val request = chatDetailInteractor.getChatMessageRequest(inputText, referenceId)
            val newResult =  async { chatDetailInteractor.addTempMessage(request, referenceId) }
            async { chatDetailInteractor.sendMessage(request) }
            val result = newResult.await()
            setContent { result }
        }
    }

    // The function to get the conversation as described.
    fun getConversation(referenceId: String) {
        viewModelScope.interactorLaunch {
            setLoading()
            val result = chatDetailInteractor.getMessages(referenceId)
            setContent { result }
        }
    }

    fun subscribeToConversation(referenceId: String) {
        // Subscribe to conversation logic
        viewModelScope.interactorLaunch {
            chatDetailInteractor.getMessagesFlow(referenceId)
                    .onEach { result ->
                       if(result.isNotEmpty()) setContent { result }
                    }.launchIn(this)
            }
    }
}