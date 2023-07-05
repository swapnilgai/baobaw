package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(private val authInteractor: AuthInteractor): BaseViewModel<AuthContent>(contentT = AuthContent()) {
    fun onSignUpClick(phoneNumber: String){
        viewModelScope.launch {
            setLoading()
            delay(5000)
            authInteractor.signUp(phoneNumber)?.let {
                setContent(it)
            }
        }
    }
}