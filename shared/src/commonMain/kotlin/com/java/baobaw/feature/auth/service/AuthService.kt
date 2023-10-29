package com.java.baobaw.feature.auth.service

import com.java.baobaw.feature.auth.model.SignUpData

interface AuthService {
    suspend fun signUp(signUpData: SignUpData)
    suspend fun login(userName: String, password: String)
}