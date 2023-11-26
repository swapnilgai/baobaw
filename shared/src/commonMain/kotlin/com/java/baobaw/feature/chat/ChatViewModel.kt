package com.java.baobaw.feature.chat

import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Transient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    @SerialName("id") val id: Int,
    @SerialName("reference_id") val referenceId: String,
    @SerialName("creator_user_id") val creatorUserId: String,
    @SerialName("message") val message: String?, // Nullable since "message" can be null
    @SerialName("created_date") val createdDate: String,
    @SerialName("seen") val seen: Boolean,
    @SerialName("is_deleted") val isDeleted: Boolean,
    @Transient val isUserCreated: Boolean = false,
    @Transient val isHeader: Boolean = false // Transient property, defaulting to false
)

class ChatViewModel(private val chatDetailInteractor: ChatDetailInteractor,
                    private val chatRealtimeInteractor: ChatRealtimeInteractor): BaseViewModel<List<ChatMessage>>(initialContent =  emptyList()) {

    fun inti(referenceId: String){
        getConversation(referenceId)
        subscribeToConversation(referenceId)
    }

    fun sendMessage(inputText: String, referenceId: String){
        viewModelScope.launch {
            setLoading()
            chatDetailInteractor.sendMessage(inputText, referenceId)
            setContent { getContent() }
        }
    }

    // The function to get the conversation as described.
    fun getConversation(referenceId: String) {
        viewModelScope.launch {
            setLoading()
            val result = chatDetailInteractor.getMessages(referenceId)
            setContent { result }
        }
    }

    fun subscribeToConversation(referenceId: String) {
        // Subscribe to conversation logic
        viewModelScope.interactorLaunch {
            val channel = chatRealtimeInteractor.getFlowStream("last_message", "reference_id=eq.$referenceId")
            channel.onEach {
                when (it) {
                    is PostgresAction.Insert -> {
                        val list = chatDetailInteractor.jsonElementToChatMessage( it.record.toString(), getContent() )
                        setContent { list }
                    }
                    is PostgresAction.Update -> {
                        val list = chatDetailInteractor.jsonElementToChatMessage( it.record.toString(), getContent() )
                        setContent { list }
                    }
                    else -> {}
                }
            }.launchIn(this)

            chatRealtimeInteractor.subscribe()
        }
    }

     override suspend fun clearViewModel(){
         viewModelScope.launch {
             chatRealtimeInteractor.unSubscribe()
         }.join()
    }
}