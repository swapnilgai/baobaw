package com.java.baobaw.feature.auth.presentation

import com.java.baobaw.AppConstants
import com.java.baobaw.feature.auth.interactor.AuthInteractor
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import com.java.baobaw.util.Preferences
import com.java.baobaw.util.getNavigationUrlWithoutBrackets

class AuthViewModel(private val authInteractor: AuthInteractor, private val preferences: Preferences, private val seasonInteractor: SeasonInteractor): BaseViewModel<AuthState>(initialContent = AuthState()) {

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
            //TODO navigate to VERIFY_OPT screen
//            navigate(
//                getNavigationUrlWithoutBrackets(AppConstants.RoutIds.VERIFY_OPT, listOf(phoneNumber, true))
//            )
            navigate(
                AppConstants.RoutIds.USER_INPUT
            )
        }
    }
    fun refreshToken(){
        viewModelScope.interactorLaunch {
            authInteractor.refreshToken()
            seasonInteractor.getCurrentUserId()?.let {
                preferences.setString(com.java.baobaw.AppConstants.Auth.CURRENT_USER, it)
            }
        }
    }
}