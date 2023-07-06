package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(private val authInteractor: AuthInteractor): BaseViewModel<AuthContent>(contentT = AuthContent()) {
    fun onSignUpClick(phoneNumber: String, password: String){
        viewModelScope.launch {
            setLoading()
            delay(500)
            authInteractor.signUp(phoneNumber, password)?.let {
                setContent(it)
            }
        }
    }

    fun sendOpt(phoneNumber: String){
        viewModelScope.launch {
            setLoading()
            authInteractor.sendOptp(phoneNumber)
            delay(500)
            setContent(getContent())
        }
    }

    fun verifyOpt(phoneNumber: String, opt: String) {
        viewModelScope.launch {
            setLoading()
            val result = authInteractor.verifyOpt(phoneNumber, opt)
            if(result!=null)
                setContent(getContent())
            else
                setError("")
        }
    }
}