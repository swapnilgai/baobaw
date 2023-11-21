package com.java.baobaw.feature.chatt_detail.di

import com.java.baobaw.feature.chatt_detail.ChatInteractor
import com.java.baobaw.feature.chatt_detail.ChatInteractorImpl
import com.java.baobaw.feature.chatt_detail.ChatViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.realtime
import org.koin.dsl.module

val chatModule = module {
    factory { get<SupabaseClient>().realtime.createChannel("messages") }
    single<ChatInteractor> { ChatInteractorImpl(get(), get(), get(), get()) }
    single<ChatViewModel> { ChatViewModel(get(), get()) }
}