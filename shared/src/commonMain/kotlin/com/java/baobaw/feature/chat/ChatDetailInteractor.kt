package com.java.baobaw.feature.chat

import androidx.compose.ui.text.intl.Locale
import com.java.baobaw.cache.MessageDetailKey
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.CacheOption
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.RetryOption
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.networkInfra.SupabaseService
import com.java.baobaw.util.decodeResultAs
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class ChatMessageResponse(
    @SerialName("id") val id: Long,
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
    suspend fun getMessages(referenceId: String, minRange: Long = 0 , maxRange: Long = 50): Map<String, List<ChatMessage>>

    suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage

    suspend fun sendMessage(inputText: String, referenceId: String): Unit

    suspend fun jsonElementToChatMessage(jsonString: String, referenceId: String): JsonLatMessageResponse

    suspend fun updateMessages(newMessages: LastMessage): Map<String, List<ChatMessage>>
}
class ChatDetailInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor) :
    ChatDetailInteractor {

    override suspend fun getMessages(referenceId: String, minRange: Long, maxRange: Long): Map<String, List<ChatMessage>>  =
        withInteractorContext(cacheOption = CacheOption(key = MessageDetailKey(referenceId = referenceId))) {
        // Fetch messages from the database
        val messages = supabaseService.select("messages") {
            eq("reference_id", referenceId)
            order("created_date", Order.DESCENDING)
            range(minRange, maxRange)
        }.decodeResultAs<List<ChatMessageResponse>>()

        val currentUserId = seasonInteractor.getCurrentUserId()

        messages.toChatMessagesWithHeadersMap(currentUserId!!)
    }

    override suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage = withInteractorContext {
        val userId = seasonInteractor.getCurrentUserId()
        jsonString.decodeResultAs<ChatMessageResponse>().toChatMessage(userId!!)
    }

    override suspend fun jsonElementToChatMessage(jsonString: String, referenceId: String): JsonLatMessageResponse =
        withInteractorContext(retryOption = RetryOption(0, objectToReturn = JsonLatMessageResponse.Error)) {
            val currentUserId = seasonInteractor.getCurrentUserId()
            val result = jsonString.decodeResultAs<LastMessageResponse>().toLastMessage(currentUserId!!)
            JsonLatMessageResponse.Success(result)
        }

    override suspend fun sendMessage(inputText: String, referenceId: String): Unit = withInteractorContext {
            val request =  getChatMessageRequest(inputText, referenceId)
            supabaseService.rpc(
                function = "insert_message",
                parameters = Json.encodeToJsonElement(request)
            )
    }

    override suspend fun updateMessages(newMessages: LastMessage): Map<String, List<ChatMessage>> =
        withInteractorContext(cacheOption = CacheOption(key = MessageDetailKey(referenceId = newMessages.referenceId), skipCache = true)) {
            updateCurrentMap(lastMessage = newMessages)
        }

    fun addTempMessage(){

    }

    suspend fun getChatMessageRequest(inputText: String, referenceId: String): ChatMessageRequest{
        val currentUserId = seasonInteractor.getCurrentUserId()
        referenceId.split(":").filterNot { it == currentUserId }.first().let { otherUserId ->
            // Send message to the other user (the user id is the second part of the reference id separated by "
            return ChatMessageRequest(
                creatorUserId = currentUserId!!,
                otherUserId = otherUserId,
                message = inputText
            )
        }
    }

    private suspend fun updateCurrentMap(lastMessage: LastMessage): Map<String, List<ChatMessage>> = withInteractorContext {
        val messagesMap = getMessages(referenceId = lastMessage.referenceId).toMutableMap()
        val userId = seasonInteractor.getCurrentUserId() ?: ""

        // Convert the last message to ChatMessage
        val newMsg = lastMessage.toChatMessage(userId)
        val messageDateHeader = lastMessage.createdDate.toChatHeaderReadableDate()

        // Check if the date header exists in the map
        if (messagesMap.containsKey(messageDateHeader)) {
            // If the date header exists, get the current list and update or add the message
            val messagesForDate = messagesMap[messageDateHeader]!!.toMutableList()
            val existingMessageIndex = messagesForDate.indexOfFirst { it.messageId == lastMessage.messageId }

            if (existingMessageIndex != -1) {
                // Update existing message
                messagesForDate[existingMessageIndex] = newMsg
            } else {
                // Add new message to the list
                messagesForDate.add(0, newMsg)
            }

            // Put the updated list back into the map
            messagesMap[messageDateHeader] = messagesForDate
        } else {
            // If the date header does not exist, create a new entry with the new message
            val firstDateHeader = messagesMap.keys.firstOrNull()
            if (firstDateHeader != null) {
                // If there are existing messages, add the new message to the top of the list
                val messagesForDate = messagesMap[firstDateHeader]!!
                val lastDate = messagesForDate[0].createdDate.parseDate()
                val newDate = newMsg.createdDate.parseDate()
                if(lastDate < newDate) {
                    return@withInteractorContext mutableMapOf(messageDateHeader to listOf(newMsg)) + messagesMap
                }
            } else {
                // If there are no existing messages, create a new entry with the new message
                messagesMap[messageDateHeader] = listOf(newMsg)
            }
        }
        return@withInteractorContext messagesMap
    }
}
fun String.parseDate(): LocalDate {
    val parts = this.split(" ")
    val day = parts[0].toInt()
    val month = Month.valueOf(parts[1].uppercase())
    val year = parts[2].toInt()
    return LocalDate(year, month, day)
}

fun List<ChatMessageResponse>.toChatMessagesWithHeadersMap(currentUserId: String): Map<String, List<ChatMessage>> {
    val timeZone = TimeZone.currentSystemDefault()
    val groupedByDate = mutableMapOf<String, MutableList<ChatMessage>>()

    this.forEach { response ->
        val messageDate = response.createdDate.toLocalDateTime(timeZone).date
        val header = messageDate.toChatHeaderReadableDate()

        val chatMessages = groupedByDate.getOrPut(header) { mutableListOf() }
        chatMessages.add(response.toChatMessage(currentUserId))
    }

    return groupedByDate
}

fun ChatMessageResponse.toChatMessage(currentUserId: String): ChatMessage {
    return ChatMessage(
        id = this.id,
        referenceId = this.referenceId,
        creatorUserId = this.creatorUserId,
        message = this.message,
        createdTime = this.createdDate.toChatReadableTime(), // Format as needed
        seen = this.seen,
        isDeleted = this.isDeleted,
        isUserCreated = this.creatorUserId == currentUserId, // Set based on user context
        isHeader = false,
        createdDate = this.createdDate.toChatHeaderReadableDate(),
        messageId = this.id.toLong()
    )
}

private fun LastMessage.toChatMessage(currentUserId : String): ChatMessage {
    return ChatMessage(
        id = this.id,
        referenceId = this.referenceId,
        creatorUserId = this.creatorUserId,
        message = this.message,
        createdTime = this.createdDate.toChatReadableTime(),
        createdDate = this.createdDate.toChatHeaderReadableDate(),
        seen = this.seen,
        isDeleted = this.isDeleted,
        isUserCreated = this.creatorUserId == currentUserId,
        isHeader = false,
        messageId = this.messageId
    )

}

private fun ChatMessageRequest.toChatMessage(currentUserId : String, referenceId: String ): ChatMessage {
    return ChatMessage(
        id = -99,
        referenceId = referenceId,
        creatorUserId = this.creatorUserId,
        message = this.message,
        createdTime =   Clock.System.now().toChatReadableTime(),
        createdDate = Clock.System.now().toChatHeaderReadableDate(),
        seen = false,
        isDeleted = false,
        isUserCreated = this.creatorUserId == currentUserId,
        isHeader = false,
        messageId = -99
    )

}



// Extension function to convert ISO date string to a readable format
fun String.toReadableDate(): String {
    val dateTime = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth} ${dateTime.month} ${dateTime.year}"
}

fun String.toChatHeaderReadableDate(): String {
    return Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault()).date.toChatHeaderReadableDate()
}

fun LocalDate.toChatHeaderReadableDate(): String {
    return "${this.dayOfMonth} ${this.month} ${this.year}"
}

fun Instant.toChatHeaderReadableDate(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.dayOfMonth} ${localDateTime.month} ${localDateTime.year}"
}

fun Instant.toChatReadableTime(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour}:${localDateTime.minute}"
}

fun String.toChatReadableTime(): String {
    val localDateTime = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour}:${localDateTime.minute}"
}