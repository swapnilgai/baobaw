package com.java.baobaw.feature.auth.presentation

import com.java.baobaw.AppConstants
import com.java.baobaw.SharedRes
import com.java.baobaw.feature.auth.interactor.AuthInteractor
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel

class LoginViewModel(private val authInteractor: AuthInteractor, private val seasonInteractor: SeasonInteractor): BaseViewModel<Unit>(initialContent = Unit) {

    fun signIn(phoneNumber: String, password: String) {
        viewModelScope.interactorLaunch {
            setLoading()
            authInteractor.signIn(phoneNumber = phoneNumber, password = password)
            val result = seasonInteractor.getCurrentSession()
            if(result?.user != null){
                navigate(
                    AppConstants.RoutIds.USER_INPUT
                )
            }
            else setError(message = SharedRes.strings.invalid_login_credentials)
        }
    }

    fun onSignUpClicked(){
        viewModelScope.interactorLaunch {
            navigate(AppConstants.RoutIds.SIGN_UP)
        }
    }

    fun onResetPasswordClicked(){
        viewModelScope.interactorLaunch {
            navigate(AppConstants.RoutIds.RESET_PASSWORD)
        }
    }

    fun onPermissionsClicked(){
        viewModelScope.interactorLaunch {
            navigate(AppConstants.RoutIds.PERMISSIONS_SCREEN)
        }
    }

    fun onChatClicked(){
        viewModelScope.interactorLaunch {
            navigate(AppConstants.RoutIds.CHAT_SCREEN)
        }
    }
}