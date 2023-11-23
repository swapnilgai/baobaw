package com.java.baobaw.util

import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.serialization.json.Json

//TODO add json injection or some other method
val json = Json {
    ignoreUnknownKeys = true // Ignores unknown keys
    coerceInputValues = true // Use default values for missing fields
}
inline fun <reified T : Any> PostgrestResult.decodeResultAs(): T = json.decodeFromString<T>(this.body.toString()?: error("No body found"))

inline fun <reified T : Any> String.decodeResultAs(): T = json.decodeFromString<T>(this?: error("No body found"))


