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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class ChatMessageResponse(
    @SerialName("id") val id: Int,
    @SerialName("reference_id") val referenceId: String,
    @SerialName("user_id_one") val userIdOne: String,
    @SerialName("user_id_two") val userIdTwo: String,
    @SerialName("creator_user_id") val creatorUserId: String,
    @SerialName("message") val message: String?, // Nullable since "message" can be null
    @SerialName("created_date") val createdDate: Instant,
    @SerialName("seen") val seen: Boolean,
    @SerialName("is_deleted") val isDeleted: Boolean,
)

@Serializable
data class ChatMessageRequest(
    @SerialName("creator_user_id") val creatorUserId: String,
    @SerialName("other_user_id") val otherUserId: String,
    @SerialName("message") val message: String?
)

interface ChatInteractor : Interactor {
    suspend fun getMessages(referenceId: String, minRange: Long = 0, maxRange: Long = 50): List<ChatMessage>
    fun getMessagesStream(referenceId: String): Flow<PostgresAction>

    suspend fun joinMessageStream()

    suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage

    suspend fun sendMessage(inputText: String)

    suspend fun unSubscribeToConversation()

    suspend fun jsonElementToChatMessage(jsonString: String, list: List<ChatMessage>): List<ChatMessage>
}
class ChatInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor) : ChatInteractor {

    override suspend fun getMessages(referenceId: String, minRange: Long, maxRange: Long): List<ChatMessage>  = withInteractorContext {
        // Fetch messages from the database
        val messages = supabaseService.select("messages") {
            eq("reference_id", referenceId)
            order("created_date", Order.DESCENDING)
            range(minRange, maxRange)
        }.decodeResultAs<List<ChatMessageResponse>>()

        val currentUserId = seasonInteractor.getCurrentUserId()

        messages.toChatMessagesWithHeaders(currentUserId!!)
        // Get current user id for comparison
    }

    override suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage = withInteractorContext {
        val userId = seasonInteractor.getCurrentUserId()
        jsonString.decodeResultAs<ChatMessageResponse>().toChatMessage(userId!!)
    }

    override suspend fun jsonElementToChatMessage(jsonString: String, list: List<ChatMessage>): List<ChatMessage> = withInteractorContext {
        val userId = seasonInteractor.getCurrentUserId()
        val msgResponse: ChatMessageResponse = jsonString.decodeResultAs<ChatMessageResponse>()
        val newMsg = msgResponse.toChatMessage(userId!!)

        val lastHeaderDate = list.lastOrNull { it.isHeader }?.createdDate
        val timeZone = TimeZone.currentSystemDefault()
        val date = msgResponse.createdDate.toLocalDateTime(timeZone).date

        val updatedList = if (lastHeaderDate == null || lastHeaderDate != date.toString() ) {
            list + createHeaderMessage(date) + newMsg
        } else {
            list + newMsg
        }
        updatedList
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



}

fun List<ChatMessageResponse>.toChatMessagesWithHeaders(currentUserId: String): List<ChatMessage> {
    val timeZone = TimeZone.currentSystemDefault()
    val messagesWithHeaders = mutableListOf<ChatMessage>()
    var lastDate: LocalDate? = null

    this.asReversed().forEach { response ->
        val messageDate = response.createdDate.toLocalDateTime(timeZone).date
        if (messageDate != lastDate) {
            lastDate = messageDate
            messagesWithHeaders.add(createHeaderMessage(messageDate))
        }
        messagesWithHeaders.add(response.toChatMessage(currentUserId))
    }
    return messagesWithHeaders
}

fun ChatMessageResponse.toChatMessage(currentUserId: String): ChatMessage {
    return ChatMessage(
        id = this.id,
        referenceId = this.referenceId,
        creatorUserId = this.creatorUserId,
        message = this.message,
        createdDate = this.createdDate.formatToReadableTime(), // Format as needed
        seen = this.seen,
        isDeleted = this.isDeleted,
        isUserCreated = this.creatorUserId == currentUserId, // Set based on user context
        isHeader = false
    )
}

private fun createHeaderMessage(date: LocalDate): ChatMessage {
    // Format date as needed for header
    return ChatMessage(
        id = 0,
        referenceId = "",
        creatorUserId = "",
        message = date.formatToReadableDate(),
        createdDate = date.toString(),
        seen = true,
        isDeleted = false,
        isHeader = true
    )
}

// Extension function to convert ISO date string to a readable format
fun String.toReadableDate(): String {
    val dateTime = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth} ${dateTime.month} ${dateTime.year}"
}

fun LocalDate.formatToReadableDate(): String {
    return "${this.dayOfMonth} ${this.month} ${this.year}"
}

fun Instant.formatToReadableDate(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.dayOfMonth} ${localDateTime.month} ${localDateTime.year}"
}

fun Instant.formatToReadableTime(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour}:${localDateTime.minute}"
}