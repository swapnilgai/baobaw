package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.model.ErrorMessage

data class AuthContent(
    val id: String = "",
    val phone: String = "",
    val confirmationMessage: String = "",
    val showLoading: Boolean = false,
    val errorMessage: ErrorMessage? = null
)
