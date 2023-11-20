package com.java.baobaw.feature.chatt_detail

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
class ChatInteractorImpl(val supabaseService: SupabaseService) : Interactor {
    suspend fun getMessages(referenceId: String, minRange: Long, maxRange: Long) : List<ChatMessage> = withInteractorContext {
            val result = supabaseService.select("messages") {
                eq("reference_id", referenceId)
                range(minRange, maxRange)
                order("created_date", Order.ASCENDING)
            }.body
            val jsonContent = (result as JsonElement).jsonPrimitive.content
            // Decode the JSON string to List<ChatMessage>
            val messages: List<ChatMessage> = Json.decodeFromString(jsonContent)
            messages
        }
}