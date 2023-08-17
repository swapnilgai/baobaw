package com.java.cherrypick.android

import android.app.Application
import com.java.cherrypick.SharedRes
import com.java.cherrypick.android.di.appModule
import com.java.cherrypick.di.initKoin
import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import com.java.cherrypick.model.ENVIRONMENT
import com.java.cherrypick.model.ProjectEnvironment
import com.onesignal.OneSignal
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class App: Application(), KoinComponent {

    private val authViewModel: AuthViewModel by inject()

    private val projectEnvironment: ProjectEnvironment by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
            appModule
        }

        authViewModel.refreshToken()
//
//        if(projectEnvironment.environment != ENVIRONMENT.PRODUCTION)
//            OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, projectEnvironment.onSignalApiKey)

//        authViewModel.getCurrentUserId()?.let {
//
//        }
    }
}