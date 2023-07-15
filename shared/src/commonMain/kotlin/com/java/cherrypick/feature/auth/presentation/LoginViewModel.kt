package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.AppConstants
import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(private val authInteractor: AuthInteractor): BaseViewModel<Unit>(initialContent = Unit) {

    fun signIn(phoneNumber: String, password: String) {
        viewModelScope.launch {
            setLoading()
            authInteractor.signIn(phoneNumber = phoneNumber, password = password)
            val result = authInteractor.getCurrentSession()
            if(result?.user != null){
                navigate(
                    AppConstants.RoutIds.userInput
                )
            }else setError("Invalid username and password")
        }
    }

    fun onSignUpClicked(){
        viewModelScope.launch {
            navigate(AppConstants.RoutIds.signUp)
        }
    }

    fun onResetPasswordClicked(){
        viewModelScope.launch {
            navigate(AppConstants.RoutIds.resetPassword)
        }
    }
}