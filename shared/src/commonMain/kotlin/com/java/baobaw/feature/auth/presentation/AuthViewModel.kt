package com.java.baobaw.feature.auth.presentation

import com.java.baobaw.AppConstants
import com.java.baobaw.feature.auth.interactor.AuthInteractor
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import com.java.baobaw.util.Preferences
import com.java.baobaw.util.getNavigationUrlWithoutBrackets
import kotlinx.coroutines.launch

class AuthViewModel(private val authInteractor: AuthInteractor, private val preferences: com.java.baobaw.util.Preferences): BaseViewModel<AuthState>(initialContent = AuthState()) {

    fun onSignUpClick(phoneNumber: String, password: String){
        viewModelScope.interactorLaunch {
            setLoading()
            authInteractor.signUp(phoneNumber, password)?.let { authContent ->
                setContent {
                    copy(
                        content = authContent
                    )
                }
            }
            navigate(
                getNavigationUrlWithoutBrackets(com.java.baobaw.AppConstants.RoutIds.verifyOpt, listOf(phoneNumber, true))
            )
        }
    }
    fun refreshToken(){
        viewModelScope.interactorLaunch {
            authInteractor.refreshToken()
            authInteractor.getCurrentUserId()?.let {
                preferences.setString(com.java.baobaw.AppConstants.Auth.currentUser, it)
            }
        }
    }
}