package com.java.baobaw.feature.chat

import com.java.baobaw.SharedRes
import com.java.baobaw.cache.CacheKey
import com.java.baobaw.cache.LastMessagesTotalCount
import com.java.baobaw.cache.MessageDetailCount
import com.java.baobaw.cache.MessageDetailKey
import com.java.baobaw.cache.PageMessageDetailKey
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.CacheOption
import com.java.baobaw.interactor.InteracroeException
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.RetryOption
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.networkInfra.SupabaseService
import com.java.baobaw.util.decodeResultAs
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
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

data class CurrentPage(
    val offset: Long,
    val totalRecords: Long
)

interface ChatDetailInteractor : Interactor {
    fun setReferenceId(referenceId: String)
    fun getMessagesFlow(referenceId: String): Flow<Map<String, List<ChatMessage>>>
    suspend fun getMessages(referenceId: String, skipCache: Boolean = true, offset: Long = 0): Map<String, List<ChatMessage>>
    suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage
    suspend fun sendMessage(chatMessageRequest: ChatMessageRequest): Unit
    suspend fun jsonElementToChatMessage(jsonString: String, referenceId: String): JsonLatMessageResponse
    suspend fun updateMessages(newMessages: LastMessage): Map<String, List<ChatMessage>>
    suspend fun addTempMessage(chatMessageRequest: ChatMessageRequest, referenceId: String) : Map<String, List<ChatMessage>>
    suspend fun getChatMessageRequest(inputText: String, referenceId: String): ChatMessageRequest
    suspend fun getMessagesTotalCount(referenceId: String): Long
    suspend fun getNextPage(isInitialPage: Boolean): Map<String, List<ChatMessage>>
    suspend fun endReached(): Boolean
}
class ChatDetailInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor) : ChatDetailInteractor {

    private var referenceId: String = ""
    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    private val message = _messages.drop(1)
    override fun setReferenceId(referenceId: String) {
        this.referenceId = referenceId
    }
    override fun getMessagesFlow(referenceId: String): Flow<Map<String, List<ChatMessage>>> = message

    private suspend fun getCurrentPage(cacheKey: CacheKey) = withInteractorContext(cacheOption = CacheOption(key = cacheKey)) {
        return@withInteractorContext CurrentPage(offset = 0, totalRecords = 0)
    }
    private suspend fun updateCurrentPage(cacheKey: CacheKey, offset: Long, totalRecords: Long) = withInteractorContext(cacheOption = CacheOption(key = cacheKey, skipCache = true)) {
        return@withInteractorContext CurrentPage(offset = offset, totalRecords = totalRecords)
    }

    override suspend fun endReached(): Boolean {
        val currentPage = getCurrentPage(PageMessageDetailKey(referenceId))
        val totalCount = getMessagesTotalCount(referenceId)
        return currentPage.offset >= totalCount
    }

    override suspend fun getMessagesTotalCount(referenceId: String): Long {
        return withInteractorContext(cacheOption = CacheOption(key = MessageDetailCount(referenceId))) {
            seasonInteractor.getCurrentUserId()?.let {currentUser ->
                supabaseService.select("messages", count = Count.EXACT, head = true) {
                    eq("reference_id", referenceId)
                }.count()?:0
            }?: 0
        }
    }

   override suspend fun getNextPage(isInitialPage: Boolean): Map<String, List<ChatMessage>> =
       withInteractorContext(cacheOption = CacheOption(key = MessageDetailKey(referenceId = referenceId), skipCache = true)) {
           val currentPage =  getCurrentPage(PageMessageDetailKey(referenceId))
           val currentContentAwait = async { getMessages(referenceId = referenceId, offset = currentPage.offset, skipCache = false) }
           val totalCountAwiat = async { getMessagesTotalCount(referenceId) }

           val currentContent =  currentContentAwait.await()
           val totalCount = totalCountAwiat.await()

           if(isInitialPage){
               updateCurrentPage(PageMessageDetailKey(referenceId), offset = currentPage.offset + currentContent.values.sumOf { it.size }.toLong(), totalRecords = totalCount)
               return@withInteractorContext currentContent
           }
        val skipCache = currentPage.offset < totalCount

        val message = getMessages(referenceId = referenceId, offset = currentPage.offset, skipCache = skipCache)

        updateCurrentPage(PageMessageDetailKey(referenceId), offset = currentPage.offset + message.values.sumOf { it.size }.toLong(), totalRecords = totalCount)

        mergeMaps(currentContent, message)

       }
    fun <K, V> mergeMaps(map1: Map<K, List<V>>, map2: Map<K, List<V>>): Map<K, List<V>> {
        val mergedMap = map1.toMutableMap()

        for ((key, value) in map2) {
            if (mergedMap.containsKey(key)) {
                // Combine the lists if the key exists in both maps
                val existingList = mergedMap[key]!!
                mergedMap[key] = existingList + value
            } else {
                // Add the new key-value pair from map2 if the key does not exist in map1
                mergedMap[key] = value
            }
        }

        return mergedMap
    }


    override suspend fun getMessages(
        referenceId: String,
        skipCache: Boolean,
        offset: Long
    ): Map<String, List<ChatMessage>> =
        withInteractorContext(cacheOption = CacheOption(key = MessageDetailKey(referenceId = referenceId), skipCache = skipCache)) {
            // Fetch messages from the database
            val messages = supabaseService.select("messages") {
                eq("reference_id", referenceId)
                order("created_date", Order.DESCENDING)
                range(offset, offset + 20)
            }.decodeResultAs<List<ChatMessageResponse>>()

            val currentUserId = seasonInteractor.getCurrentUserId()

            messages.toChatMessagesWithHeadersMap(currentUserId!!)
        }

    override suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage =
        withInteractorContext {
            val userId = seasonInteractor.getCurrentUserId()
            jsonString.decodeResultAs<ChatMessageResponse>().toChatMessage(userId!!)
        }

    override suspend fun jsonElementToChatMessage(
        jsonString: String,
        referenceId: String
    ): JsonLatMessageResponse =
        withInteractorContext(
            retryOption = RetryOption(
                0,
                objectToReturn = JsonLatMessageResponse.Error
            )
        ) {
            val currentUserId = seasonInteractor.getCurrentUserId()
            val result =
                jsonString.decodeResultAs<LastMessageResponse>().toLastMessage(currentUserId!!)
            JsonLatMessageResponse.Success(result)
        }

    override suspend fun sendMessage(chatMessageRequest: ChatMessageRequest): Unit =
        withInteractorContext {
            if(chatMessageRequest.message?.isNotEmpty() == true)
            supabaseService.rpc(
                function = "insert_message",
                parameters = Json.encodeToJsonElement(chatMessageRequest)
            )
        }

    override suspend fun updateMessages(newMessages: LastMessage): Map<String, List<ChatMessage>> =
        withInteractorContext(
            cacheOption = CacheOption(
                key = MessageDetailKey(referenceId = newMessages.referenceId),
                skipCache = true
            )
        ) {
            val result = updateCurrentMap(lastMessage = newMessages)
            if(referenceId == newMessages.referenceId) {
                _messages.value = result
            }
            incrementMessageCount(referenceId)
            result
        }

    override suspend fun addTempMessage(
        chatMessageRequest: ChatMessageRequest,
        referenceId: String
    ): Map<String, List<ChatMessage>> =
        withInteractorContext(
            cacheOption = CacheOption(
                key = MessageDetailKey(referenceId = referenceId),
                skipCache = true
            )
        ) {
            val lastMessage = chatMessageRequest.toLastMessage(referenceId)
            updateCurrentMap(lastMessage, false)
        }

    override suspend fun getChatMessageRequest(
        inputText: String,
        referenceId: String
    ): ChatMessageRequest {
        val currentUserId = seasonInteractor.getCurrentUserId()

        // Check if referenceId can be split into two parts
        val parts = referenceId.split(":")
        if (parts.size != 2) {
            throw InteracroeException.Generic(SharedRes.strings.token_has_expired_or_is_invalid)
            //TODO write exception message
        }

        // Extract the other user's ID, ensuring it's not the current user's ID
        val otherUserId = parts.firstOrNull { it != currentUserId }

        // Throw an exception if otherUserId is null or empty
        if (otherUserId.isNullOrEmpty()) {
            throw InteracroeException.Generic(SharedRes.strings.token_has_expired_or_is_invalid)
            //TODO write exception message
        }

        // Send message to the other user
        return ChatMessageRequest(
            creatorUserId = currentUserId!!,
            otherUserId = otherUserId,
            message = inputText
        )
    }

    private suspend fun updateCurrentMap(lastMessage: LastMessage, sent: Boolean = true): Map<String, List<ChatMessage>> = withInteractorContext {
        val messagesMap = getMessages(referenceId = lastMessage.referenceId, skipCache = false).toMutableMap()
        val userId = seasonInteractor.getCurrentUserId() ?: ""

        // Convert the last message to ChatMessage
        val newMsg = lastMessage.toChatMessage(userId, sent)
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
                // Filter messages with negative messageId
                val negativeIdMessages = messagesForDate.filter { it.messageId < 0 }

                // Check if any of these messages match the lastMessage's data
                val matchingMessage = negativeIdMessages.firstOrNull {
                    it.message == lastMessage.message && it.creatorUserId == lastMessage.creatorUserId && !it.sent
                }

                if (matchingMessage != null) {
                    // Update the matching message
                    val indexToUpdate = messagesForDate.indexOf(matchingMessage)
                    messagesForDate[indexToUpdate] = newMsg
                } else {
                    // Add new message to the list
                    messagesForDate.add(0, newMsg)
                }
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

    suspend fun incrementMessageCount(referenceId: String) {
         withInteractorContext(cacheOption = CacheOption(key = MessageDetailCount(referenceId), skipCache = true)) {
            val result = if(!endReached()) getMessagesTotalCount(referenceId) + 1 else getMessagesTotalCount(referenceId)
             result
        }
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
        messageId = this.id.toLong(),
        sent = true
    )
}

private fun LastMessage.toChatMessage(currentUserId : String, sent: Boolean): ChatMessage {
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
        messageId = this.messageId,
        sent = sent
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

private fun ChatMessageRequest.toLastMessage(referenceId: String): LastMessage {
    val id = NegativeNumberGenerator.getNext()
    return LastMessage(
        id = id,
        referenceId = referenceId,
        creatorUserId = this.creatorUserId,
        message = this.message!!,
        createdDate = Clock.System.now().toString(),
        seen = false,
        isDeleted = false,
        messageId = id,
        imageUrl = "",
        name = "",
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

object NegativeNumberGenerator {
    private var currentNumber = 0L

    fun getNext(): Long {
        currentNumber--
        return currentNumber
    }
}
