package com.java.cherrypick.android

import android.app.Application
import com.java.cherrypick.AppConstants
import com.java.cherrypick.android.di.appModule
import com.java.cherrypick.di.initKoin
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import com.java.cherrypick.model.ENVIRONMENT
import com.java.cherrypick.model.ProjectEnvironment
import com.onesignal.OneSignal
import com.java.cherrypick.util.Preferences
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import com.onesignal.debug.LogLevel


class App: Application(), KoinComponent {
    private val projectEnvironment: ProjectEnvironment by inject()
    private val preferences: Preferences by inject()
    private val authViewModel: AuthViewModel by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
            appModule
        }

        if(projectEnvironment.environment != ENVIRONMENT.PRODUCTION)
            OneSignal.Debug.logLevel = LogLevel.VERBOSE

        authViewModel.refreshToken().let {
            preferences.getString(AppConstants.Auth.currentUser)?.let {currentUser ->
                OneSignal.initWithContext(this, projectEnvironment.onSignalApiKey)
                OneSignal.login(currentUser)
                OneSignal.User.pushSubscription.optIn()
            }
        }

    }



}