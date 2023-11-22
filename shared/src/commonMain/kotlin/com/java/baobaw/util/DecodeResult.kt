package com.java.baobaw.util

import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.serialization.json.Json

inline fun <reified T : Any> PostgrestResult.decodeResultAs(): T = Json.decodeFromString<T>(this.body.toString()?: error("No body found"))

inline fun <reified T : Any> String.decodeResultAs(): T = Json.decodeFromString<T>(this?: error("No body found"))


