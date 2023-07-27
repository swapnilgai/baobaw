package com.java.cherrypick

object AppConstants {

    object RoutIds{
        const val signUp = "signUp"
        const val verifyOpt = "verifyOpt"
        const val userInput = "userInput"
        const val login = "login"
        const val resetPassword = "resetPassword"
        const val userLocation = "userLocation"
    }

    object Auth{
        const val otpCount = 6
    }

    object NavigationParam{
        const val phoneNumber = "phoneNumber"
        const val sendOpt = "sendOpt"
    }

    object Queries{
        val userExistWithPhone  = "user_exist_with_phone"
    }
}