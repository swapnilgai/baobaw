package com.java.baobaw.feature.auth.interactor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneExist(@SerialName("phone_number") val phoneNumber: String)

fun String.toPhoneExist() = PhoneExist(phoneNumber = this)

fun String.toBoolean() = this.lowercase() == "true"

fun String.numberOnly() = "[^A-Za-z0-9 ]".toRegex().replace(this, "")

