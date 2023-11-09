package com.java.baobaw.feature.auth.presentation

import com.java.baobaw.AppConstants
import com.java.baobaw.SharedRes
import com.java.baobaw.feature.auth.interactor.AuthInteractor
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import com.java.baobaw.util.getNavigationUrlWithoutBrackets

class ResetPasswordViewModel(private val authInteractor: AuthInteractor): BaseViewModel<UserExist>(initialContent = UserExist()) {

    fun phoneExists(phoneNumber: String){
        viewModelScope.interactorLaunch {
            setLoading()
            val phoneExists = authInteractor.phoneExists(phoneNumber) ?: false
            if(!phoneExists)
                setError(message = SharedRes.strings.user_does_not_exist_please_sign_up)
            else navigate(getNavigationUrlWithoutBrackets(AppConstants.RoutIds.VERIFY_OPT, listOf(phoneNumber, true)))

        }
    }
}