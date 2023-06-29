package com.java.cherrypick.feature.auth.service

import com.java.cherrypick.feature.auth.model.SignUpData

interface AuthService {
    suspend fun signUp(signUpData: SignUpData)
    suspend fun login(userName: String, password: String)
}