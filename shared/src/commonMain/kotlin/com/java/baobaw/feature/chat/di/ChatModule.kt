package com.java.baobaw.feature.chat.di

import com.java.baobaw.feature.chat.ChatDetailInteractor
import com.java.baobaw.feature.chat.ChatDetailInteractorImpl
import com.java.baobaw.feature.chat.ChatListInteractor
import com.java.baobaw.feature.chat.ChatListInteractorImpl
import com.java.baobaw.feature.chat.ChatListViewModel
import com.java.baobaw.feature.chat.ChatRealtimeInteractor
import com.java.baobaw.feature.chat.ChatRealtimeInteractorImpl
import com.java.baobaw.feature.chat.ChatDetailViewModel
import org.koin.dsl.module

val chatModule = module {
    single<ChatDetailInteractor> { ChatDetailInteractorImpl(get(), get()) }
    single<ChatRealtimeInteractor> { ChatRealtimeInteractorImpl(get(), get(), get()) }
    single<ChatListInteractor> { ChatListInteractorImpl(get(), get()) }
    single<ChatDetailViewModel> { ChatDetailViewModel(get(), get()) }
    single<ChatListViewModel> { ChatListViewModel(get(), get()) }
}
