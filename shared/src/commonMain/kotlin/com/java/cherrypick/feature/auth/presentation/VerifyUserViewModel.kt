package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.AppConstants
import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.presentationInfra.BaseViewModel
import kotlinx.coroutines.launch

class VerifyUserViewModel(private val authInteractor: AuthInteractor): BaseViewModel<VerifyUserState>(initialContent = VerifyUserState()) {

    fun sendOpt(phoneNumber: String){
        viewModelScope.launch {
            setLoading()
            authInteractor.sendOptp(phoneNumber)
        }
    }

    fun verifyOpt(phoneNumber: String, opt: String) {
        viewModelScope.launch {
            setLoading()
            authInteractor.verifyOpt(phoneNumber = phoneNumber, opt = opt)
            val result = authInteractor.getCurrentSession()
            if(result?.user != null){
                navigate(
                    AppConstants.RoutIds.userInput
                )
            }else setError("Invalid Opt or expired opt")
            //TODO add error messages to app constant inorder to map from android and ios to support localization
        }
    }
}