package com.java.baobaw.feature.chat

import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
    val messageId: Long
)

class ChatDetailViewModel(private val chatDetailInteractor: ChatDetailInteractor,
                          private val chatRealtimeInteractor: ChatRealtimeInteractor): BaseViewModel<Map<String, List<ChatMessage>>>(initialContent =  emptyMap()) {

    fun init(referenceId: String){
        viewModelScope.interactorLaunch {
           async {  getConversation(referenceId) }
           async {  subscribeToConversation(referenceId) }
        }
    }

    fun sendMessage(inputText: String, referenceId: String){
        viewModelScope.interactorLaunch {
            setLoading()
            chatDetailInteractor.sendMessage(inputText, referenceId)
            setContent { getContent() }
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
            setLoading()
            val isConnected =  chatRealtimeInteractor.isConnected(ChatListType.DETAIL_MESSAGES)
            if(!isConnected) {
                chatRealtimeInteractor.subscribeToConversation(referenceId)
                    .onEach { newMessage ->
                        val result = chatDetailInteractor.updateMessages(newMessage)
                        setContent { result }
                    }.launchIn(this)
            }
            if(getContent().isNotEmpty()) setContent { getContent() }
        }
    }

     override suspend fun clearViewModel(){
         viewModelScope.interactorLaunch {
             chatRealtimeInteractor.unSubscribe(chatListType = ChatListType.DETAIL_MESSAGES)
         }.join()
    }
}