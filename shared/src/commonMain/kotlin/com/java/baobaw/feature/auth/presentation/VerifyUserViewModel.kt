package com.java.baobaw.feature.auth.presentation

import com.java.baobaw.AppConstants
import com.java.baobaw.SharedRes
import com.java.baobaw.feature.auth.interactor.AuthInteractor
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyUserViewModel(private val authInteractor: AuthInteractor): BaseViewModel<VerifyUserState>(initialContent = VerifyUserState()) {

    fun sendOpt(phoneNumber: String){
        viewModelScope.interactorLaunch {
            setLoading()
            authInteractor.sendOptp(phoneNumber)
            delay(100)
            setContent { getContent() }
        }
    }

    fun verifyOpt(phoneNumber: String, opt: String) {
        viewModelScope.interactorLaunch {
            setLoading()
            authInteractor.verifyOpt(phoneNumber = phoneNumber, opt = opt)
            val result = authInteractor.getCurrentSession()
            if(result?.user != null){
                navigate(
                    com.java.baobaw.AppConstants.RoutIds.userInput
                )
            }
            else setError(message = com.java.baobaw.SharedRes.strings.token_has_expired_or_is_invalid)
        }
    }
}