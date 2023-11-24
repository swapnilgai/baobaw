package com.java.baobaw.feature.chat

import com.java.baobaw.cache.AuthSessionCacheKey
import com.java.baobaw.cache.LastMessagesTotalCount
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.CacheOption
import com.java.baobaw.interactor.Interactor
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
    suspend fun getLastMessages(pageNumber: Long, pageSize: Long): PaginatedResponse<LastMessage>

}
class ChatListInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor): ChatListInteractor {

    override suspend fun getLastMessagesTotalCount(): Long {
        return withInteractorContext(cacheOption = CacheOption(key = LastMessagesTotalCount())) {
            val currentUser =
                seasonInteractor.getCurrentUserId()!! // Replace with actual function to get current user ID
            supabaseService.select("last_message", count = Count.EXACT, head = true) {
                or {
                    eq("user_id_one", currentUser)
                    eq("user_id_one", currentUser)
                }
            }.decodeResultAs<Long>()
        }
    }

    override suspend fun getLastMessages(
        pageNumber: Long,
        pageSize: Long
    ): PaginatedResponse<LastMessage> {
        return withInteractorContext {
            // Fetch messages from the database
            val currentUser = seasonInteractor.getCurrentUserId()!! // Replace with actual function to get current user ID

            val offset = (pageNumber - 1) * pageSize

            val listMessageResponseAwait = async {
                supabaseService.select("last_message") {
                    or {
                        eq("user_id_one", currentUser)
                        eq("user_id_one", currentUser)
                    }
                    order("created_date", Order.DESCENDING)
                    limit(
                        pageSize,
                        offset.toString()
                    )  // Use limit for pageSize and offset for skipping records
                }
            }
            val countAwait = async { getLastMessagesTotalCount() }

            val totalRecords = countAwait.await()
            val listMessageResponse = listMessageResponseAwait.await().decodeResultAs<List<LastMessageResponse>>()

            PaginatedResponse(
                data = listMessageResponse.toLastMessages(currentUser),
                pageNumber = pageNumber,
                pageSize = pageSize,
                totalPages = ceil(totalRecords / pageSize.toDouble()).toInt()
            )
        }
    }
}
fun List<LastMessageResponse>.toLastMessages(currentUser: String): List<LastMessage> {
    return this.map { response ->
        LastMessage(
            id = response.id,
            referenceId = response.referenceId,
            creatorUserId = response.creatorUserId,
            imageUrl = if (currentUser == response.userIdOne) response.userIdTwoImageUrl ?: "" else response.userIdOneImageUrl,
            name = if (currentUser == response.userIdOne) response.userIdTwoName else response.userIdOneName,
            message = if (response.isDeleted) "Deleted Message" else response.message,
            isDeleted = response.isDeleted,
            seen = if (currentUser == response.creatorUserId) true else response.seen,
            createdDate = response.createdDate
        )
    }
}

