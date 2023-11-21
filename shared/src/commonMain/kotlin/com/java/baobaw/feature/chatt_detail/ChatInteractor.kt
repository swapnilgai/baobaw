package com.java.baobaw.feature.chatt_detail

import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.networkInfra.SupabaseService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

interface ChatInteractor : Interactor {
    suspend fun getMessages(referenceId: String, minRange: Long = 0, maxRange: Long = 20): List<ChatMessage>
}
class ChatInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor) : ChatInteractor {
    val json = Json {
         ignoreUnknownKeys = true
    }

    override suspend fun getMessages(referenceId: String, minRange: Long, maxRange: Long): List<ChatMessage> = withInteractorContext {
        val messages : List<ChatMessage> = supabaseService.select("messages") {
            eq("reference_id", referenceId)
            range(minRange, maxRange)
            order("created_date", Order.ASCENDING)
        }.decodeList()

        // Set isUserCreated property
        val currentUserId = seasonInteractor.getCurrentUserId()
        messages.map { it.copy(isUserCreated = it.creatorUserId == currentUserId) }
   }
}
