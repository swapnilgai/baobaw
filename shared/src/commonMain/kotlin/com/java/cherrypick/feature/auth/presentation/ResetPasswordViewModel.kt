package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.AppConstants
import com.java.cherrypick.SharedRes
import kotlinx.coroutines.launch
import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.interactor.interactorLaunch
import com.java.cherrypick.presentationInfra.BaseViewModel
import com.java.cherrypick.util.getNavigationUrlWithoutBrackets

class ResetPasswordViewModel(private val authInteractor: AuthInteractor): BaseViewModel<UserExist>(initialContent = UserExist()) {

    fun phoneExists(phoneNumber: String){
        viewModelScope.interactorLaunch {
            setLoading()
            val phoneExists = authInteractor.phoneExists(phoneNumber) ?: false
            if(!phoneExists)
                setError(message = SharedRes.strings.user_does_not_exist_please_sign_up)
            else navigate(getNavigationUrlWithoutBrackets(AppConstants.RoutIds.verifyOpt, listOf(phoneNumber, true)))

        }
    }
}