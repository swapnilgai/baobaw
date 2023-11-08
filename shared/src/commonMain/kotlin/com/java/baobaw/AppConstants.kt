package com.java.baobaw

object AppConstants {

    object RoutIds{
        const val SIGN_UP = "signUp"
        const val VERIFY_OPT = "verifyOpt"
        const val USER_INPUT = "userInput"
        const val LOGIN = "login"
        const val RESET_PASSWORD = "resetPassword"
        const val PERMISSIONS_SCREEN = "permissionsScreen"
        const val IMAGE_PICKER_SCREEN = "imagePickerScreen"
    }

    object Auth{
        const val otpCount = 6
        const val CURRENT_USER = "currentUser"
    }

    object NavigationParam{
        const val PHONE_NUMBER = "phoneNumber"
        const val SEND_OPT = "sendOpt"
    }

    object Queries{
        const val PHONE_EXISTS  = "phone_exists"
        const val  IS_IMAGE_APPROPRIATE = "is_image_appropriate"
    }
}