package com.java.cherrypick.android

import android.app.Application
import com.java.cherrypick.AppConstants
import com.java.cherrypick.android.di.appModule
import com.java.cherrypick.di.initKoin
import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import com.java.cherrypick.util.Preferences
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent

class App: Application(), KoinComponent {

    private val preferences: Preferences by inject()
    private val authViewModel: AuthViewModel by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
            appModule
        }

        authViewModel.refreshToken().let {
            val currentUser = preferences.getString(AppConstants.Auth.currentUser)
        }
    }
}