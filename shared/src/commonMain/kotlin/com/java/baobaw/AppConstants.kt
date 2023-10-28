package com.java.baobaw

object AppConstants {

    object RoutIds{
        const val signUp = "signUp"
        const val verifyOpt = "verifyOpt"
        const val userInput = "userInput"
        const val login = "login"
        const val resetPassword = "resetPassword"
        const val permissionsScreen = "permissionsScreen"
        const val imagePickerScreen = "imagePickerScreen"
    }

    object Auth{
        const val otpCount = 6
        const val currentUser = "currentUser"
    }

    object NavigationParam{
        const val phoneNumber = "phoneNumber"
        const val sendOpt = "sendOpt"
    }

    object Queries{
        val userExistWithPhone  = "phone_exists"
    }
}