package com.java.baobaw.feature.auth.presentation

import com.java.baobaw.AppConstants
import com.java.baobaw.SharedRes
import com.java.baobaw.feature.auth.interactor.AuthInteractor
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(private val authInteractor: AuthInteractor, private val seasonInteractor: SeasonInteractor): BaseViewModel<Unit>(initialContent = Unit) {

    fun signIn(phoneNumber: String, password: String) {
        viewModelScope.interactorLaunch {
            setLoading()
            authInteractor.signIn(phoneNumber = phoneNumber, password = password)
            val result = seasonInteractor.getCurrentSession()
            if(result?.user != null){
                navigate(
                    com.java.baobaw.AppConstants.RoutIds.userInput
                )
            }
            else setError(message = com.java.baobaw.SharedRes.strings.invalid_login_credentials)
        }
    }

    fun onSignUpClicked(){
        viewModelScope.interactorLaunch {
            navigate(com.java.baobaw.AppConstants.RoutIds.signUp)
        }
    }

    fun onResetPasswordClicked(){
        viewModelScope.interactorLaunch {
            navigate(com.java.baobaw.AppConstants.RoutIds.resetPassword)
        }
    }

    fun onPermissionsClicked(){
        viewModelScope.interactorLaunch {
            navigate(com.java.baobaw.AppConstants.RoutIds.permissionsScreen)
        }
    }
}