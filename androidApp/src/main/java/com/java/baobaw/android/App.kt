package com.java.baobaw.android

import android.app.Application
import com.java.baobaw.AppConstants
import com.java.baobaw.android.di.appModule
import com.java.baobaw.di.initKoin
import com.java.baobaw.feature.auth.presentation.AuthViewModel
import com.java.baobaw.model.ENVIRONMENT
import com.java.baobaw.model.ProjectEnvironment
import com.onesignal.OneSignal
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import com.onesignal.debug.LogLevel


class App: Application(), KoinComponent {
    private val projectEnvironment: ProjectEnvironment by inject()
    private val preferences: com.java.baobaw.util.Preferences by inject()
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
            preferences.getString(AppConstants.Auth.CURRENT_USER)?.let { currentUser ->
                OneSignal.initWithContext(this, projectEnvironment.onSignalApiKey)
                OneSignal.login(currentUser)
                OneSignal.User.pushSubscription.optIn()
            }
        }

    }
}