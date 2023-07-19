package com.java.cherrypick.android.navigation

import com.java.cherrypick.AppConstants

sealed class Screens(val route: String) {
    object SignUp: Screens(AppConstants.RoutIds.signUp)
    object VerifyOpt: Screens(AppConstants.RoutIds.verifyOpt)
    object UserInput: Screens(AppConstants.RoutIds.userInput)
    object Login: Screens(AppConstants.RoutIds.login)
    object ResetPassword: Screens(AppConstants.RoutIds.resetPassword)
}