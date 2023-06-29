package com.java.cherrypick.networkInfra

import com.java.cherrypick.model.ProductionEnvironment
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
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

class ApiClient(productionEnvironment: ProductionEnvironment) {

    val client = HttpClient {
        defaultRequest {
            url {
                 takeFrom(productionEnvironment.url)
                 parameters.append("api_key", productionEnvironment.apiKey)
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
        supabaseUrl = productionEnvironment.url,
        supabaseKey = productionEnvironment.apiKey
    ) {
        install(GoTrue)
        install(Postgrest)
        //install other modules
    }
}