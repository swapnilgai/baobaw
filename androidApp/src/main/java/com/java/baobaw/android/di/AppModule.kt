package com.java.baobaw.android.di

import com.java.baobaw.android.notification.PushNotificationBuilder
import org.koin.dsl.module

val appModule = module {
    single<PushNotificationBuilder> { PushNotificationBuilder(context = get(), resources = get()) }
}