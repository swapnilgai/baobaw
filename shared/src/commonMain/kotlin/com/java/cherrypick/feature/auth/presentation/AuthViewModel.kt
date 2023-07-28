package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.AppConstants
import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel
import com.java.cherrypick.util.getNavigationUrlWithoutBrackets
import kotlinx.coroutines.launch

class AuthViewModel(private val authInteractor: AuthInteractor): BaseViewModel<AuthState>(initialContent = AuthState()) {

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
}