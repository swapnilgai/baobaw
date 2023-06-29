package com.java.cherrypick.di

import com.java.cherrypick.feature.auth.di.authModule
import com.java.cherrypick.networkInfra.di.networkModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
            networkModule,
            authModule,
        )
    }

fun initKoin() = initKoin {}
