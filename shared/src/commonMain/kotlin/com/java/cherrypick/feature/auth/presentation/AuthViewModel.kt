package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.AppConstants
import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel
import com.java.cherrypick.util.Preferences
import com.java.cherrypick.util.getNavigationUrlWithoutBrackets
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.launch

class AuthViewModel(private val authInteractor: AuthInteractor, private val preferences: Preferences): BaseViewModel<AuthState>(initialContent = AuthState()) {

    fun onSignUpClick(phoneNumber: String, password: String){
        viewModelScope.launch {
            setLoading()
            authInteractor.signUp(phoneNumber, password)?.let { authContent ->
                setContent {
                    copy(
                        content = authContent
                    )
                }
            }
            navigate(
                getNavigationUrlWithoutBrackets(AppConstants.RoutIds.verifyOpt, listOf(phoneNumber, true))
            )
        }
    }
    fun refreshToken(){
        viewModelScope.launch {
            authInteractor.refreshToken()
            authInteractor.getCurrentUserId()?.let {
                preferences.setString(AppConstants.Auth.currentUser, it)
            }
        }
    }
}