package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.AppConstants
import com.java.cherrypick.SharedRes
import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.interactor.interactorLaunch
import com.java.cherrypick.presentationInfra.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(private val authInteractor: AuthInteractor): BaseViewModel<Unit>(initialContent = Unit) {

    fun signIn(phoneNumber: String, password: String) {
        viewModelScope.interactorLaunch {
            setLoading()
            authInteractor.signIn(phoneNumber = phoneNumber, password = password)
            val result = authInteractor.getCurrentSession()
            if(result?.user != null){
                navigate(
                    AppConstants.RoutIds.userInput
                )
            }
            else setError(message = SharedRes.strings.invalid_login_credentials)
        }
    }

    fun onSignUpClicked(){
        viewModelScope.interactorLaunch {
            navigate(AppConstants.RoutIds.signUp)
        }
    }

    fun onResetPasswordClicked(){
        viewModelScope.interactorLaunch {
            navigate(AppConstants.RoutIds.resetPassword)
        }
    }

    fun onPermissionsClicked(){
        viewModelScope.interactorLaunch {
            navigate(AppConstants.RoutIds.permissionsScreen)
        }
    }
}