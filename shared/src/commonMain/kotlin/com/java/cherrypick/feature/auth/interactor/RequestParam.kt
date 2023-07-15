package com.java.cherrypick.feature.auth.interactor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneExist(@SerialName("phone_number") val phoneNumber: String)

fun String.toPhoneExist() = PhoneExist(phoneNumber = this)