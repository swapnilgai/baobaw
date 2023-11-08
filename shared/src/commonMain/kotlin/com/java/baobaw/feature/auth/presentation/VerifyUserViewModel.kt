package com.java.baobaw.feature.auth.presentation

import com.java.baobaw.AppConstants
import com.java.baobaw.SharedRes
import com.java.baobaw.feature.auth.interactor.AuthInteractor
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import kotlinx.coroutines.delay

class VerifyUserViewModel(private val authInteractor: AuthInteractor, private val seasonInteractor: SeasonInteractor): BaseViewModel<VerifyUserState>(initialContent = VerifyUserState()) {

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
            val result = seasonInteractor.getCurrentSession()
            if(result?.user != null){
                navigate(
                    AppConstants.RoutIds.USER_INPUT
                )
            }
            else setError(message = SharedRes.strings.token_has_expired_or_is_invalid)
        }
    }
}