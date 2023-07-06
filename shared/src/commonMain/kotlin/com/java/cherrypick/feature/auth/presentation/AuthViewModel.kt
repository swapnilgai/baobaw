package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(private val authInteractor: AuthInteractor): BaseViewModel<AuthState>(state = AuthState()) {
    fun onSignUpClick(phoneNumber: String, password: String){
        viewModelScope.launch {
            setLoading()
            authInteractor.signUp(phoneNumber, password)?.let { authContent ->
                setContent(
                    getContent().copy(
                        content = authContent,
                        showLoading = false,
                        errorMessage = null
                    )
                )
            }
        }
    }

    fun sendOpt(phoneNumber: String){
        viewModelScope.launch {
            setLoading()
            authInteractor.sendOptp(phoneNumber)
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
    fun onDismissClicked(){
        setContent(
            getContent().copy( showLoading = false, errorMessage = null)
        )
    }
}