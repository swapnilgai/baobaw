package com.java.cherrypick.feature.auth.presentation

data class AuthState(
    val content: AuthContent? = null,
)

data class AuthContent( val id: String = "",
                        val phone: String = "",
                        val confirmationMessage: String = "")