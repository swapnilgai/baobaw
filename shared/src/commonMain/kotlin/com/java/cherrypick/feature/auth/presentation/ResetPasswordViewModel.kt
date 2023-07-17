package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.AppConstants
import kotlinx.coroutines.launch

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel
import com.java.cherrypick.util.getNavigationUrlWithoutBrackets

class ResetPasswordViewModel(private val authInteractor: AuthInteractor): BaseViewModel<UserExist>(initialContent = UserExist()) {

    fun phoneExists(phoneNumber: String){
        viewModelScope.launch {
            setLoading()
            val phoneExists = authInteractor.phoneExists(phoneNumber) ?: false
            if(phoneExists)
                setError("User already exist")
            else
                navigate(
                    getNavigationUrlWithoutBrackets(AppConstants.RoutIds.verifyOpt, listOf(phoneNumber))
                )
        }
    }
}