package com.java.baobaw.feature.chatt_detail

import com.java.baobaw.presentationInfra.BaseViewModel
import io.github.jan.supabase.SupabaseClient
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
    @SerialName("user_id_one") val userIdOne: String,
    @SerialName("user_id_two") val userIdTwo: String,
    @SerialName("creator_user_id") val creatorUserId: String,
    @SerialName("message") val message: String?, // Nullable since "message" can be null
    @SerialName("created_date") val createdDate: String,
    @SerialName("seen") val seen: Boolean,
    @SerialName("is_deleted") val isDeleted: Boolean,
    @Transient val isUserCreated: Boolean = false // Transient property, defaulting to false
)


class ChatViewModel(private val chatInteractor: ChatInteractor, private val supabaseClient: SupabaseClient): BaseViewModel<List<ChatMessage>>(initialContent =  emptyList()) {


    fun init(){
        getConversation("1")
        subscribeToConversation("1")
    }

    fun fetchPreviousDataPage() {
        // Fetch next page logic
    }

    // The function to get the conversation as described.
    fun getConversation(referenceId: String) {
        viewModelScope.launch {
            val result = chatInteractor.getMessages(referenceId)
            setContent { result }
        }
    }

    fun subscribeToConversation(referenceId: String) {
        // Subscribe to conversation logic
        viewModelScope.launch {
            val channel = chatInteractor.getMessagesStream(referenceId)
            channel.onEach {
                when (it) {
                    is PostgresAction.Insert -> chatInteractor.jsonElementToChatMessage( it.record.toString()).let { chatMessage ->
                            setContent { getContent() + chatMessage }
                        }
                    is PostgresAction.Update -> chatInteractor.jsonElementToChatMessage( it.record.toString()).let { chatMessage ->
                            setContent { getContent() + chatMessage }
                    }
                    else -> {}
                }
            }.launchIn(this)

            chatInteractor.joinMessageStream()
        }
    }
}