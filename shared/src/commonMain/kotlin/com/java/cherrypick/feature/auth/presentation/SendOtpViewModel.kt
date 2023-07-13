package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.AppConstants
import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel
import com.java.cherrypick.util.getNavigationUrlWithoutBrackets
import kotlinx.coroutines.launch

class SendOtpViewModel(private val authInteractor: AuthInteractor): BaseViewModel<AuthState>(initialContent = AuthState()) {

    fun sendOtpTo(phoneNumber: String){
        viewModelScope.launch {
            setLoading()
            authInteractor.sendOtpTo(phoneNumber)
            navigate(
                getNavigationUrlWithoutBrackets(AppConstants.RoutIds.verifyOpt, listOf(phoneNumber))
            )
        }
    }
}