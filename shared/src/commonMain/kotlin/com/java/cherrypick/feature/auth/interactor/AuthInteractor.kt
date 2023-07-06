package com.java.cherrypick.feature.auth.interactor

import com.java.cherrypick.feature.auth.model.SignUpData
import com.java.cherrypick.feature.auth.presentation.AuthContent
import com.java.cherrypick.interactor.Interactor
import com.java.cherrypick.interactor.withInteractorContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.Phone
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface AuthInteractor: Interactor {
    suspend fun signUp(signUpData: SignUpData): Email.Result?
    suspend fun signUp(phoneNumber: String, password: String): AuthContent?
    suspend fun login(userName: String, password: String)
    suspend fun verifyOpt(opt: String, phoneNumber: String): UserSession?
    suspend fun sendOptp(phoneNumber: String): Unit

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

    override suspend fun signUp(phoneNumber: String, password: String): AuthContent? {
        return withInteractorContext {
            supabaseClient.gotrue.signUpWith(Phone) {
                this.phoneNumber = phoneNumber
                this.password = password
            }?.let { result ->
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


    override suspend fun sendOptp(phoneNumber: String): Unit {
        withInteractorContext {
            supabaseClient.gotrue.sendOtpTo(Phone) {
                this.phoneNumber = phoneNumber
            }
        }
    }

    override suspend fun verifyOpt(opt: String, phoneNumber: String) : UserSession? {
       return withInteractorContext {
            supabaseClient.gotrue.verifyPhoneOtp(
                type = OtpType.Phone.SMS,
                phoneNumber = phoneNumber,
                token = opt
            )
            supabaseClient.gotrue.currentSessionOrNull()
        }
    }
}