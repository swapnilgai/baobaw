package com.java.baobaw.feature.chat

import com.java.baobaw.cache.LastMessagesTotalCount
import com.java.baobaw.cache.UserMessagesKey
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.CacheOption
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.invalidateCache
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.networkInfra.SupabaseService
import com.java.baobaw.util.decodeResultAs
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlin.math.ceil

@Serializable
data class LastMessageResponse(
    @SerialName("id") val id: Long,
    @SerialName("reference_id") val referenceId: String,
    @SerialName("user_id_one") val userIdOne: String,
    @SerialName("user_id_two") val userIdTwo: String,
    @SerialName("creator_user_id") val creatorUserId: String,
    @SerialName("user_id_one_image_url") val userIdOneImageUrl: String,
    @SerialName("user_id_two_image_url") val userIdTwoImageUrl: String?,
    @SerialName("user_id_one_name") val userIdOneName: String,
    @SerialName("user_id_two_name") val userIdTwoName: String,
    @SerialName("message") val message: String,
    @SerialName("is_deleted") val isDeleted: Boolean,
    @SerialName("is_blocked") val isBlocked: Boolean,
    @SerialName("seen") val seen: Boolean,
    @SerialName("created_date") val createdDate: String,
)
@Serializable
data class LastMessage(
    val id: Long,
    val referenceId: String,
    val creatorUserId: String,
    val imageUrl: String,
    val name: String,
    val message: String,
    val isDeleted: Boolean,
    val seen: Boolean,
    val createdDate: String
)

data class PaginatedResponse<T>(
    val data: List<T>,
    val pageNumber: Long,
    val pageSize: Long,
    val totalPages: Int
)

interface ChatListInteractor : Interactor{
    suspend fun getLastMessagesTotalCount(): Long
    suspend fun getLastMessages(pageNumber: Long, pageSize: Long, currentMessages: List<LastMessage> = emptyList()): PaginatedResponse<LastMessage>
    suspend fun jsonElementToLastMessage(jsonString: String): LastMessage
    suspend fun invalidateMessageCache(pageNumber: Long = 1, pageSize: Long = 20L)
    suspend fun updateMessagesWithNewData(currentMessages: List<LastMessage>, newMessages: List<LastMessage>): List<LastMessage>
}
class ChatListInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor): ChatListInteractor {

    override suspend fun getLastMessagesTotalCount(): Long {
        return withInteractorContext(cacheOption = CacheOption(key = LastMessagesTotalCount("LastMessagesTotalCount"))) {
            seasonInteractor.getCurrentUserId()?.let {currentUser ->
                supabaseService.select("last_message", count = Count.EXACT, head = true) {
                    or {
                        eq("user_id_one", currentUser)
                        eq("user_id_two", currentUser)
                    }
                }.count()?:0
            }?: 0
        }
    }

    override suspend fun getLastMessages(
        pageNumber: Long,
        pageSize: Long,
        currentMessages: List<LastMessage>
    ): PaginatedResponse<LastMessage> {
        return withInteractorContext(cacheOption = CacheOption(key = UserMessagesKey(pageSize = pageSize, pageNumber = pageNumber))) {
            // Fetch messages from the database
            val currentUser = seasonInteractor.getCurrentUserId()!! // Replace with actual function to get current user ID

            val offset = pageNumber * pageSize

            val listMessageResponseAwait = async {
                supabaseService.select("last_message") {
                    or {
                        eq("user_id_one", currentUser)
                        eq("user_id_two", currentUser)
                    }
                    limit(offset)
                    order("created_date", Order.DESCENDING)
                }
            }
            val countAwait = async { getLastMessagesTotalCount() }

            val totalRecords = countAwait.await()
            val listMessageResponse = listMessageResponseAwait.await().decodeResultAs<List<LastMessageResponse>>()

            val lastMessageList = listMessageResponse.toLastMessages(currentUser)

            val combinedMessages = updateMessagesWithNewData(currentMessages, lastMessageList)

            PaginatedResponse(
                data = combinedMessages,
                pageNumber = pageNumber,
                pageSize = pageSize,
                totalPages = ceil(totalRecords / pageSize.toDouble()).toInt()
            )
        }
    }

    override suspend fun jsonElementToLastMessage(jsonString: String): LastMessage = withInteractorContext {
        val userId = seasonInteractor.getCurrentUserId()
        jsonString.decodeResultAs<LastMessageResponse>().toLastMessage(userId!!)
    }

    override suspend fun invalidateMessageCache(pageNumber: Long, pageSize: Long) = withInteractorContext {
        invalidateCache(UserMessagesKey(pageNumber = pageNumber, pageSize = pageSize))
    }

    override suspend fun updateMessagesWithNewData(
        currentMessages: List<LastMessage>,
        newMessages: List<LastMessage>
    ): List<LastMessage> {
        val currentMessageIds = currentMessages.map { it.id }.toSet()
        val updatedMessagesMap = newMessages.associateBy { it.id }

        val combinedMessages = mutableListOf<LastMessage>()

        // Update existing messages and keep track of which new messages have been used
        currentMessages.forEach { message ->
            val updatedMessage = updatedMessagesMap[message.id]
            combinedMessages.add(updatedMessage ?: message)
        }

        // Add new messages that weren't already in the current messages
        newMessages.forEach { message ->
            if (message.id !in currentMessageIds) {
                combinedMessages.add(message)
            }
        }
        return combinedMessages
    }
}
fun List<LastMessageResponse>.toLastMessages(currentUser: String): List<LastMessage> {
    return this.map { response -> response.toLastMessage(currentUser) }
}

fun LastMessageResponse.toLastMessage(currentUser: String): LastMessage {
    return LastMessage(
        id = this.id,
        referenceId = this.referenceId,
        creatorUserId = this.creatorUserId,
        imageUrl = if (currentUser == this.userIdOne) this.userIdTwoImageUrl ?: "" else this.userIdOneImageUrl,
        name = if (currentUser == this.userIdOne) this.userIdTwoName else this.userIdOneName,
        message = if (this.isDeleted) "Deleted Message" else this.message,
        isDeleted = this.isDeleted,
        seen = if (currentUser == this.creatorUserId) true else this.seen,
        createdDate = this.createdDate
    )
}

