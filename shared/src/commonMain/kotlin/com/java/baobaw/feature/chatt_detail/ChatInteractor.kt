package com.java.baobaw.feature.chatt_detail

import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.networkInfra.SupabaseService
import com.java.baobaw.util.decodeResultAs
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

interface ChatInteractor : Interactor {
    suspend fun getMessages(referenceId: String, minRange: Long = 0, maxRange: Long = 10): List<ChatMessage>
    fun getMessagesStream(referenceId: String): Flow<PostgresAction>

    suspend fun joinMessageStream()

    suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage

    suspend fun sendMessage(inputText: String)

    suspend fun unSubscribeToConversation()
}
class ChatInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor) : ChatInteractor {

    override suspend fun getMessages(referenceId: String, minRange: Long, maxRange: Long): List<ChatMessage> = withInteractorContext {
        val messages = supabaseService.select("messages") {
            eq("reference_id", referenceId)
            order("created_date", Order.DESCENDING)
            range(minRange, maxRange)
        }.decodeResultAs<List<ChatMessage>>().asReversed()
        // Set isUserCreated property
        val currentUserId = seasonInteractor.getCurrentUserId()
        messages.map { it.copy(isUserCreated = it.creatorUserId == currentUserId) }
   }

    override fun getMessagesStream(referenceId: String): Flow<PostgresAction> {
        val realtimeChannel = supabaseService.getMessageRealtimeChannel()
        val changeFlow = realtimeChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "messages"
            filter = "reference_id=eq.$referenceId"
        }
        return changeFlow
    }

    override suspend fun joinMessageStream() {
        supabaseService.realTimeConnect()
        supabaseService.getMessageRealtimeChannel().join()
    }

    override suspend fun unSubscribeToConversation() {
        supabaseService.realTimeDisconnect()
        supabaseService.getMessageRealtimeChannel().let {
            it.leave()
            supabaseService.realtimeRemoveChannel(it)
        }
    }

    override suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage = withInteractorContext {
        jsonString.decodeResultAs<ChatMessage>()
    }

    override suspend fun sendMessage(inputText: String): Unit = withInteractorContext {
        val currentUserId = seasonInteractor.getCurrentUserId()
        val request = ChatMessageRequest(
            creatorUserId = currentUserId!!,
            otherUserId = "a1b2c3d4-782c-4ccb-816c-62d012345685",
            message = inputText
        )
        supabaseService.rpc(
            function = "insert_message",
            parameters = Json.encodeToJsonElement(request)
        )
    }
}

@Serializable
data class ChatMessageRequest(
    @SerialName("creator_user_id") val creatorUserId: String,
    @SerialName("other_user_id") val otherUserId: String,
    @SerialName("message") val message: String?
)
