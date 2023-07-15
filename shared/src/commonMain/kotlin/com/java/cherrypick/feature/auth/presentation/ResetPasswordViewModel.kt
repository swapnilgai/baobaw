package com.java.cherrypick.feature.auth.presentation

import kotlinx.coroutines.launch

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel

class ResetPasswordViewModel(private val authInteractor: AuthInteractor): BaseViewModel<UserExist>(initialContent = UserExist()) {

    fun phoneExists(phoneNumber: String){
        viewModelScope.launch {
            setLoading()
            val result = authInteractor.phoneExists(phoneNumber) ?: false
            setContent { getContent().copy(isUserExist = result) }
        }
    }
}