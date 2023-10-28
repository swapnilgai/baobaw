package com.java.baobaw.android.navigation

import com.java.baobaw.AppConstants

sealed class Screens(val route: String) {
    object SignUp: Screens(com.java.baobaw.AppConstants.RoutIds.signUp)
    object VerifyOpt: Screens(com.java.baobaw.AppConstants.RoutIds.verifyOpt)
    object UserInput: Screens(com.java.baobaw.AppConstants.RoutIds.userInput)
    object Login: Screens(com.java.baobaw.AppConstants.RoutIds.login)
    object ResetPassword: Screens(com.java.baobaw.AppConstants.RoutIds.resetPassword)
    object PermissionsScreen: Screens(com.java.baobaw.AppConstants.RoutIds.permissionsScreen)
    object ImagePickerScreen: Screens(com.java.baobaw.AppConstants.RoutIds.imagePickerScreen)

}