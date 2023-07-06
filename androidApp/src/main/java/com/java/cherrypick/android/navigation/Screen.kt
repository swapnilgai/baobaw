package com.java.cherrypick.android.navigation

sealed class Screen(val route: String) {
    object SignUpScreen: Screen("sign_up_screen")
    object VerifyOptScreen: Screen("verify_opt_screen")
}