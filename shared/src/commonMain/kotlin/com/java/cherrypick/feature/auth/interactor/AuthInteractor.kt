package com.java.cherrypick.feature.auth.interactor

import com.java.cherrypick.feature.auth.model.SignUpData
import com.java.cherrypick.feature.auth.presentation.AuthContent
import com.java.cherrypick.interactor.Interactor
import com.java.cherrypick.interactor.withInteractorContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.Phone
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface AuthInteractor: Interactor {
    suspend fun signUp(signUpData: SignUpData): Email.Result?
    suspend fun signUp(phoneNumber: String): AuthContent?
    suspend fun login(userName: String, password: String)
}

class AuthInteractorImple(private val supabaseClient: SupabaseClient): AuthInteractor {
    override suspend fun signUp(signUpData: SignUpData): Email.Result? {
       return withInteractorContext {
            supabaseClient.gotrue.signUpWith(Email) {
                email = signUpData.email
                password = signUpData.password
                data = buildJsonObject {
                    put("firstName", signUpData.firstName)
                    put("age", signUpData.age.toInt())
                }
            }
        }
    }

    override suspend fun signUp(phoneNumber: String): AuthContent? {
       return withInteractorContext {
       supabaseClient.gotrue.signUpWith(Phone) {
               this.phoneNumber = phoneNumber
               password = "password"
           }?.let {result ->
               AuthContent(
                   id = result.id,
                   phone = result.phone,
                   confirmationMessage = "${result.confirmationSentAt.toString()}",
               )
           }
       }
    }

    override suspend fun login(userName: String, password: String) {
        TODO("Not yet implemented")
    }

}