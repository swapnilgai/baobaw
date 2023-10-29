package com.java.baobaw.networkInfra.di

import com.java.baobaw.networkInfra.ApiClient
import io.github.jan.supabase.SupabaseClient
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> { ApiClient(get()).client }
    single<SupabaseClient> { ApiClient(get()).supabaseClient }
}