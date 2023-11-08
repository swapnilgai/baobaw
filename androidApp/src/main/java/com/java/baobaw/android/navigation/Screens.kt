package com.java.baobaw.android.navigation

sealed class Screens(val route: String) {
    object SignUp: Screens(com.java.baobaw.AppConstants.RoutIds.SIGN_UP)
    object VerifyOpt: Screens(com.java.baobaw.AppConstants.RoutIds.VERIFY_OPT)
    object UserInput: Screens(com.java.baobaw.AppConstants.RoutIds.USER_INPUT)
    object Login: Screens(com.java.baobaw.AppConstants.RoutIds.LOGIN)
    object ResetPassword: Screens(com.java.baobaw.AppConstants.RoutIds.RESET_PASSWORD)
    object PermissionsScreen: Screens(com.java.baobaw.AppConstants.RoutIds.PERMISSIONS_SCREEN)
    object ImagePickerScreen: Screens(com.java.baobaw.AppConstants.RoutIds.IMAGE_PICKER_SCREEN)

}