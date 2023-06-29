package com.java.cherrypick.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.dsl.module

class App: Application(), KoinComponent {

    override fun onCreate() {

        super.onCreate()
        initKoin {
            androidContext(this@App)
        }
    }
}