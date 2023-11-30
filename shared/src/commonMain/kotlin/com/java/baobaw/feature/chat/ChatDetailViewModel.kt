package com.java.baobaw.feature.chat

import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import io.github.jan.supabase.realtime.PostgresAction
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
    val messageId: Long
)

class ChatDetailViewModel(private val chatDetailInteractor: ChatDetailInteractor,
                          private val chatRealtimeInteractor: ChatRealtimeInteractor): BaseViewModel<List<ChatMessage>>(initialContent =  emptyList()) {

    fun init(referenceId: String){
        getConversation(referenceId)
        subscribeToConversation(referenceId)
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
            val channel = chatRealtimeInteractor.getFlowStream("last_message", "reference_id=eq.$referenceId", ChatListType.DETAIL_MESSAGES)
            channel.onEach {
                when (it) {
                    is PostgresAction.Insert -> {
                        when(val result = chatDetailInteractor.jsonElementToChatMessage(it.record.toString(), referenceId))
                        {
                            is JsonChatMessageResponse.Success -> {
                                setContent { result.listChatMessage }
                            }
                            else -> {}
                        }
                    }
                    is PostgresAction.Update -> {
                        when(val result = chatDetailInteractor.jsonElementToChatMessage(it.record.toString(), referenceId))
                        {
                            is JsonChatMessageResponse.Success -> {
                                setContent { result.listChatMessage }
                            }
                            else -> {}
                        }
                    }
                    else -> {}
                }
            }.launchIn(this)
            chatRealtimeInteractor.subscribe(chatListType = ChatListType.DETAIL_MESSAGES)
        }
    }

     override suspend fun clearViewModel(){
         viewModelScope.interactorLaunch {
             chatRealtimeInteractor.unSubscribe(chatListType = ChatListType.DETAIL_MESSAGES)
         }.join()
    }
}