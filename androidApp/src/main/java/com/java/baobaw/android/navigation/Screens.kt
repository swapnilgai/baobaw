package com.java.baobaw.android.navigation

import com.java.baobaw.AppConstants.RoutIds.CHAT_LIST_SCREEN
import com.java.baobaw.AppConstants.RoutIds.CHAT_DETAIL_SCREEN
import com.java.baobaw.AppConstants.RoutIds.IMAGE_PICKER_SCREEN
import com.java.baobaw.AppConstants.RoutIds.LOGIN
import com.java.baobaw.AppConstants.RoutIds.PERMISSIONS_SCREEN
import com.java.baobaw.AppConstants.RoutIds.RESET_PASSWORD
import com.java.baobaw.AppConstants.RoutIds.SIGN_UP
import com.java.baobaw.AppConstants.RoutIds.USER_INPUT
import com.java.baobaw.AppConstants.RoutIds.VERIFY_OPT

sealed class Screens(val route: String) {
    data object SignUp: Screens(SIGN_UP)
    data object VerifyOpt: Screens(VERIFY_OPT)
    data object UserInput: Screens(USER_INPUT)
    data object Login: Screens(LOGIN)
    data object ResetPassword: Screens(RESET_PASSWORD)
    data object PermissionsScreen: Screens(PERMISSIONS_SCREEN)
    data object ImagePickerScreen: Screens(IMAGE_PICKER_SCREEN)
    data object ChatScreen: Screens(CHAT_DETAIL_SCREEN)
    data object ChatListScreen: Screens(CHAT_LIST_SCREEN)

}