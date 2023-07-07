package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.model.ErrorMessage
import com.java.cherrypick.presentationInfra.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(private val authInteractor: AuthInteractor): BaseViewModel<AuthState>(state = AuthState()) {

    override var error: (String) -> Unit = {err-> setState { copy(errorMessage = ErrorMessage(err)) }}
    override var loading: () -> Unit = { setState{ copy(showLoading = true) } }

    fun onSignUpClick(phoneNumber: String, password: String){
        viewModelScope.launch {
            loading.invoke()
            authInteractor.signUp(phoneNumber, password)?.let { authContent ->
                setState {
                    copy(
                        content = authContent,
                        showLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun sendOpt(phoneNumber: String){
        viewModelScope.launch {
            loading.invoke()
            authInteractor.sendOptp(phoneNumber)
        }
    }

    fun verifyOpt(phoneNumber: String, opt: String) {
        viewModelScope.launch {
            loading.invoke()
            val result = authInteractor.verifyOpt(phoneNumber, opt)
        }
    }
    fun onDismissClicked(){
        setState { copy(showLoading = false, errorMessage = null) }
    }

}