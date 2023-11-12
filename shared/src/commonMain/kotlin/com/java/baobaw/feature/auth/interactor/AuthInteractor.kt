package com.java.baobaw.feature.auth.interactor

import com.java.baobaw.AppConstants
import com.java.baobaw.cache.UserExistCacheKey
import com.java.baobaw.feature.auth.model.SignUpData
import com.java.baobaw.feature.auth.presentation.AuthContent
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.CacheOption
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.RetryOption
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.util.Preferences
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.Phone
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

interface AuthInteractor: Interactor {
    suspend fun signUp(signUpData: SignUpData): Email.Result?
    suspend fun signUp(phoneNumber: String, password: String): AuthContent?
    suspend fun login(userName: String, password: String)
    suspend fun verifyOpt(opt: String, phoneNumber: String)
    suspend fun sendOptp(phoneNumber: String): Unit
    suspend fun logOut()
    suspend fun signIn(phoneNumber: String, password: String)
    suspend fun phoneExists(phoneNumber: String): Boolean?
    suspend fun refreshToken()
}

class AuthInteractorImple(private val supabaseClient: SupabaseClient, val seasonInteractive: SeasonInteractor,private val preferences: Preferences): AuthInteractor {

    private val authMutex = Mutex()
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
            val str = phoneNumber.removePrefix("+")
//            supabaseClient.gotrue.signUpWith(Phone) {
//                this.phoneNumber = phoneNumber
//                this.password = password
//            }
            supabaseClient.gotrue.signUpWith(Email) {
                this.email = "swa@yahoo.com"
                this.password = password
            }?.let { result ->
                AuthContent(
                    id = result.id,
                    phone = "",
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

    override suspend fun verifyOpt(opt: String, phoneNumber: String)  {
        withInteractorContext {
            supabaseClient.gotrue.verifyPhoneOtp(
                type = OtpType.Phone.SMS,
                phoneNumber = phoneNumber,
                token = opt
            )
        }
    }

    override suspend fun logOut() {
        withInteractorContext {
            supabaseClient.gotrue.logout()
        }
    }

    override suspend fun signIn(phoneNumber: String, password: String) {
        withInteractorContext {
            supabaseClient.gotrue.loginWith(Email) {
                this.email = "swa@yahoo.com"
                this.password = password
            }
//            supabaseClient.gotrue.loginWith(Phone) {
//                this.phoneNumber = phoneNumber
//                this.password = password
//            }
        }
    }

    override suspend fun phoneExists(phoneNumber: String): Boolean? {
        return withInteractorContext() {
            val result =  supabaseClient.postgrest.rpc(AppConstants.Queries.PHONE_EXISTS, phoneNumber.numberOnly().toPhoneExist()).body
            (result as JsonElement).jsonPrimitive.content.toBoolean()
        }
    }

    override suspend fun refreshToken() {
        return withInteractorContext(retryOption = RetryOption(retryCount = 3)) {
            val currentSession = supabaseClient.gotrue.currentSessionOrNull()
            if(currentSession!=null)
            currentSession.refreshToken.let{
                //TODO update shared preference with secure storage
                preferences.setString(AppConstants.Auth.CURRENT_USER, it)
            }else{
                preferences.getString(AppConstants.Auth.CURRENT_USER, "").let {
                    if(it.isNotBlank()) supabaseClient.gotrue.refreshSession(it)
                }
            }
        }
    }
}

