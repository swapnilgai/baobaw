package com.java.cherrypick.android

import android.app.Application
import com.java.cherrypick.android.di.appModule
import com.java.cherrypick.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent

class App: Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
            appModule
        }
    }
}