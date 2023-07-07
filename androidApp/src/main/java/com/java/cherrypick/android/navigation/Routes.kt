package com.java.cherrypick.android.navigation

import com.java.cherrypick.AppConstants

sealed class Routes(val route: String) {
    object SignUp: Routes(AppConstants.RoutIds.signUp)
    object VerifyOpt: Routes(AppConstants.RoutIds.verifyOpt)
}