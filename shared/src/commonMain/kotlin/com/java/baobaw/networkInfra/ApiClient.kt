package com.java.baobaw.networkInfra

import com.java.baobaw.model.ProjectEnvironment
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient(projectEnvironment: ProjectEnvironment) {

    val client = HttpClient {
        defaultRequest {
            url {
                 takeFrom(projectEnvironment.url)
                 parameters.append("api_key", projectEnvironment.apiKey)
            }
        }
        expectSuccess = true
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    val supabaseClient = createSupabaseClient(
        supabaseUrl = projectEnvironment.url,
        supabaseKey = projectEnvironment.apiKey
    ) {
        install(GoTrue)
        install(Postgrest)
        install(Storage)
        install(Realtime)
        //install other modules
    }
}