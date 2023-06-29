package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.feature.auth.model.SignUpData
import com.java.cherrypick.presentationInfra.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(val authInteractor: AuthInteractor): BaseViewModel<AuthContent>(contentT = AuthContent(shoeContent = false)) {
    fun onSignUpClick(){
        viewModelScope.launch {
            authInteractor.signUp(
                signUpData = SignUpData(
                    firstName = "fname ios",
                    lastName = "lname ios",
                    email = "ios@yaho.com",
                    age = "22",
                    password = "test@532514111",
                    phone = "7174049049"
                )
            )
        }
    }
}