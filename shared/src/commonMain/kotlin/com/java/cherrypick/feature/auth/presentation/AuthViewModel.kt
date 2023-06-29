package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel

class AuthViewModel(authInteractor: AuthInteractor): BaseViewModel<AuthContent>(contentT = AuthContent(shoeContent = false)) {
    fun onSignUpClick(){
        setLoading()
    }
}