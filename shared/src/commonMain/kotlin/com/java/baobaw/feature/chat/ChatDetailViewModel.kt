package com.java.baobaw.feature.chat

import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import com.java.baobaw.util.DefaultPaginator
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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

data class ChatDetailContent(val data: Map<String, List<ChatMessage>> = emptyMap(), val totalLoadedRecords : Int =0)
class ChatDetailViewModel(private val chatDetailInteractor: ChatDetailInteractor): BaseViewModel<ChatDetailContent>(initialContent =  ChatDetailContent()) {
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
            async { chatDetailInteractor.sendMessage(request) }
            val result = newResult.await()
            setContent { getContent().copy(data = result) }
        }
    }

    // The function to get the conversation as described.
    fun getConversation(isInitial: Boolean = false) {
        if(isLoading) return
        if(isInitial) setCustomLoading()
        viewModelScope.interactorLaunch {
            isLoading = true
            val endReached = chatDetailInteractor.endReached()
            if(!endReached || isInitial) {
                setCustomLoading()
                val result = chatDetailInteractor.getNextPage(isInitial)
                val count = result.values.sumOf { it.size }
                setContent { getContent().copy(data = result, totalLoadedRecords = count) }
            }
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
        super.clearViewModel()
        isLoading = false
    }
}