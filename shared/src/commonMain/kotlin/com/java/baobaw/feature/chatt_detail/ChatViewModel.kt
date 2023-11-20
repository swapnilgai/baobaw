package com.java.baobaw.feature.chatt_detail

import com.java.baobaw.feature.upload.presentation.ImageSelectionContent
import com.java.baobaw.presentationInfra.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun parseUtcDateTime(utcDateTimeString: String): Instant {
    // Assuming the format is "yyyy-MM-dd HH:mm:ss.SSSSSS±HH"
    // Modify the string to match the ISO-8601 format for parsing
    val formattedString = utcDateTimeString.replace(" ", "T") + "Z"
    return Instant.parse(formattedString)
}

fun convertToUserTimeZone(utcInstant: Instant): LocalDateTime {
    val userTimeZone = TimeZone.currentSystemDefault()
    return utcInstant.toLocalDateTime(userTimeZone)
}

@Serializable
data class ChatMessage(
    val id: String,
    @SerialName("reference_id") val referenceId: String,
    @SerialName("creator_user_id") val creatorUserId: String,
    val message: String,
    @SerialName("created_date") val createdDate: Instant,
    val seen: Boolean,
    @SerialName("is_deleted") val isDeleted: Boolean
)

class ChatViewModel(private val chatInteractor: ChatInteractor): BaseViewModel<Unit>(initialContent =  Unit) {

    fun fetchPreviousDataPage() {
        // Fetch next page logic
    }

    // The function to get the conversation as described.
    fun getConversation(referenceId: String) {
        viewModelScope.launch {
            val result = chatInteractor.getMessages(referenceId)
            val res = result.size
        }
    }
}