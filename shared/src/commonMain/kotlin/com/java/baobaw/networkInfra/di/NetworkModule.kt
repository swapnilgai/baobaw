package com.java.baobaw.networkInfra.di

import com.java.baobaw.networkInfra.ApiClient
import com.java.baobaw.networkInfra.SupabaseService
import com.java.baobaw.networkInfra.SupabaseServiceImpl
import io.github.jan.supabase.SupabaseClient
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> { ApiClient(get()).client }
    single<SupabaseClient> { ApiClient(get()).supabaseClient }
    single<SupabaseService> { SupabaseServiceImpl(get()) }
}