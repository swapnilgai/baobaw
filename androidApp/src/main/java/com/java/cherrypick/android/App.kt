package com.java.cherrypick.android

import android.app.Application
import com.java.cherrypick.android.di.appModule
import com.java.cherrypick.di.initKoin
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import com.java.cherrypick.model.ProjectEnvironment
import com.onesignal.OneSignal
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class App: Application(), KoinComponent {
    private val projectEnvironment: ProjectEnvironment by inject()
    private val authViewModel: AuthViewModel by inject()
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
            appModule
        }
        OneSignal.initWithContext(this)
    }
}