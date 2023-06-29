package com.java.cherrypick.networkInfra.di

import com.java.cherrypick.networkInfra.ApiClient
import io.github.jan.supabase.SupabaseClient
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> { ApiClient(get()).client }
    single<SupabaseClient> { ApiClient(get()).supabaseClient }
}