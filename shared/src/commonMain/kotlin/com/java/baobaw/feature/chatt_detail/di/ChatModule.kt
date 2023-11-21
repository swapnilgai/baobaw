package com.java.baobaw.feature.chatt_detail.di

import com.java.baobaw.feature.chatt_detail.ChatInteractor
import com.java.baobaw.feature.chatt_detail.ChatInteractorImpl
import com.java.baobaw.feature.chatt_detail.ChatViewModel
import org.koin.dsl.module

val chatModule = module {
    single<ChatInteractor> { ChatInteractorImpl(get(), get()) }
    single<ChatViewModel> { ChatViewModel(get()) }
}