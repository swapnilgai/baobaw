package com.java.cherrypick.network.di

import com.java.cherrypick.network.ApiClient
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> { ApiClient(get()).client }
}