package com.java.baobaw.feature.chat

import com.java.baobaw.cache.MessageDetailKey
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.CacheOption
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.networkInfra.SupabaseService
import com.java.baobaw.util.decodeResultAs
import io.github.jan.supabase.postgrest.query.Order
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

interface ChatDetailInteractor : Interactor {
    suspend fun getMessages(referenceId: String, minRange: Long = 0 , maxRange: Long = 50): List<ChatMessage>

    suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage

    suspend fun sendMessage(inputText: String, referenceId: String): Unit

    suspend fun jsonElementToChatMessage(jsonString: String, referenceId: String): List<ChatMessage>

    suspend fun updateMessages(newMessages: LastMessage): List<ChatMessage>
}
class ChatDetailInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor) :
    ChatDetailInteractor {

    override suspend fun getMessages(referenceId: String, minRange: Long, maxRange: Long): List<ChatMessage>  =
        withInteractorContext(cacheOption = CacheOption(key = MessageDetailKey(referenceId = referenceId))) {
        // Fetch messages from the database
        val messages = supabaseService.select("messages") {
            eq("reference_id", referenceId)
            order("created_date", Order.DESCENDING)
            range(minRange, maxRange)
        }.decodeResultAs<List<ChatMessageResponse>>()

        val currentUserId = seasonInteractor.getCurrentUserId()

        messages.toChatMessagesWithHeaders(currentUserId!!)
    }

    override suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage = withInteractorContext {
        val userId = seasonInteractor.getCurrentUserId()
        jsonString.decodeResultAs<ChatMessageResponse>().toChatMessage(userId!!)
    }

    override suspend fun jsonElementToChatMessage(jsonString: String, referenceId: String): List<ChatMessage> =
        withInteractorContext(cacheOption = CacheOption(key = MessageDetailKey(referenceId = referenceId,), skipCache = true)) {
            updateCurrentList(jsonString.decodeResultAs<LastMessage>())
    }

    override suspend fun sendMessage(inputText: String, referenceId: String): Unit = withInteractorContext {
        val currentUserId = seasonInteractor.getCurrentUserId()
        referenceId.split(":").filterNot { it == currentUserId }.firstOrNull()?.let { otherUserId ->
            // Send message to the other user (the user id is the second part of the reference id separated by "
            val request = ChatMessageRequest(
                creatorUserId = currentUserId!!,
                otherUserId = otherUserId,
                message = inputText
            )
            supabaseService.rpc(
                function = "insert_message",
                parameters = Json.encodeToJsonElement(request)
            )
        }
    }

    override suspend fun updateMessages(newMessages: LastMessage): List<ChatMessage> =
        withInteractorContext(cacheOption = CacheOption(key = MessageDetailKey(referenceId = newMessages.referenceId,), skipCache = true)) {
            updateCurrentList(lastMessage = newMessages)
        }

    private suspend fun updateCurrentList(lastMessage: LastMessage) : List<ChatMessage> = withInteractorContext {
        val list = getMessages(referenceId = lastMessage.referenceId)
        val userId = seasonInteractor.getCurrentUserId()

        val lastHeaderDate = list.lastOrNull { it.isHeader }?.createdTime
        val date = lastMessage.createdDate.formatToReadableDate()
        val newMsg = lastMessage.toChatMessage(userId!!)

        val updatedList = if (lastHeaderDate == null || lastHeaderDate != date.toString() ) {
            list + createHeaderMessage(date) + newMsg
        } else {
            list + newMsg
        }
        updatedList
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
            messagesWithHeaders.add(createHeaderMessage(messageDate.formatToReadableDate()))
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
        createdTime = this.createdDate.formatToReadableTime(), // Format as needed
        seen = this.seen,
        isDeleted = this.isDeleted,
        isUserCreated = this.creatorUserId == currentUserId, // Set based on user context
        isHeader = false,
        createdDate = this.createdDate.formatToReadableDate()
    )
}

private fun LastMessage.toChatMessage(currentUserId : String): ChatMessage {
    return ChatMessage(
        id = this.id.toInt(),
        referenceId = this.referenceId,
        creatorUserId = this.creatorUserId,
        message = this.message,
        createdTime = this.createdDate.formatToReadableTime(),
        createdDate = this.createdDate.formatToReadableDate(),
        seen = this.seen,
        isDeleted = this.isDeleted,
        isUserCreated = this.creatorUserId == currentUserId,
        isHeader = false
    )

}

private fun createHeaderMessage(date: String): ChatMessage {
    // Format date as needed for header
    return ChatMessage(
        id = 0,
        referenceId = "",
        creatorUserId = "",
        message = date,
        createdTime = date,
        seen = true,
        isDeleted = false,
        isHeader = true,
        createdDate = date
    )
}

// Extension function to convert ISO date string to a readable format
fun String.toReadableDate(): String {
    val dateTime = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth} ${dateTime.month} ${dateTime.year}"
}

fun String.formatToReadableDate(): String {
    return Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault()).date.formatToReadableDate()
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

fun String.formatToReadableTime(): String {
    val localDateTime = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour}:${localDateTime.minute}"
}