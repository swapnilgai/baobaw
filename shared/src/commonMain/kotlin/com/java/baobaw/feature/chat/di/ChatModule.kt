package com.java.baobaw.feature.chat.di

import com.java.baobaw.feature.chat.ChatDetailInteractor
import com.java.baobaw.feature.chat.ChatDetailInteractorImpl
import com.java.baobaw.feature.chat.ChatRealtimeInteractor
import com.java.baobaw.feature.chat.ChatRealtimeInteractorImpl
import com.java.baobaw.feature.chat.ChatViewModel
import org.koin.dsl.module

val chatModule = module {
    single<ChatDetailInteractor> { ChatDetailInteractorImpl(get(), get()) }
    single<ChatRealtimeInteractor> { ChatRealtimeInteractorImpl(get(), get()) }
    single<ChatViewModel> { ChatViewModel(get(), get()) }
}