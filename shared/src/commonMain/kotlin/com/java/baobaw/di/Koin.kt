package com.java.baobaw.di

import com.java.baobaw.feature.auth.di.authModule
import com.java.baobaw.feature.auth.di.permissionModule
import com.java.baobaw.feature.chat.di.chatModule
import com.java.baobaw.feature.common.di.commonModule
import com.java.baobaw.feature.location.di.locationModule
import com.java.baobaw.feature.upload.di.uploadModule
import com.java.baobaw.networkInfra.di.networkModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
            networkModule,
            commonModule,
            authModule,
            permissionModule,
            platformModule(),
            uploadModule,
            locationModule,
            chatModule
        )
    }

fun initKoin() = initKoin {}
