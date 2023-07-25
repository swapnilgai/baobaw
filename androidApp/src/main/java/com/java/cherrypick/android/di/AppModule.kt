package com.java.cherrypick.android.di

import com.java.cherrypick.android.notification.PushNotificationBuilder
import org.koin.dsl.module

val appModule = module {
    single<PushNotificationBuilder> { PushNotificationBuilder(context = get(), resources = get()) }
}