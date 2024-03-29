package com.java.baobaw.interactor

import com.java.baobaw.SharedRes
import dev.icerock.moko.resources.StringResource
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.SupabaseEncodingException
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.NotFoundRestException


sealed class InteracroeException : Exception() {
    abstract val messageRes: StringResource
    sealed class HTTP: InteracroeException() {
        data class ServerError internal constructor(override val messageRes: StringResource): HTTP()

        data class Unauthorized internal constructor(override val messageRes: StringResource): HTTP()

        data class BadRequestRest internal constructor(override val messageRes: StringResource): HTTP()

    }
    data class Generic constructor(override val messageRes: StringResource): InteracroeException()
}

//TODO update exception handling
fun Exception.toInteractorException(): InteracroeException {
    return when(this){
        is HttpRequestException -> InteracroeException.HTTP.ServerError(messageRes = this.message.supabaseToInteractorException())
        is UnauthorizedRestException -> InteracroeException.HTTP.Unauthorized(messageRes = this.getSupabaseErrorMessage().supabaseToInteractorException())
        is BadRequestRestException -> InteracroeException.HTTP.BadRequestRest(messageRes = this.getSupabaseErrorMessage().supabaseToInteractorException())
        is NotFoundRestException -> InteracroeException.HTTP.BadRequestRest(messageRes = this.getSupabaseErrorMessage().supabaseToInteractorException())
        else -> InteracroeException.Generic(messageRes = this.message.supabaseToInteractorException())
    }
}

fun RestException.getSupabaseErrorMessage() = if(!this.description.isNullOrBlank()) this.description else this.error


/**
 * Supabase does not support localization for errors so in order to support localization,
 * we need to map strings from SharedRes which supports localization at shared module level
 */
fun String?.supabaseToInteractorException(): StringResource = when(this?.lowercase()) {
        "to signup, please provide your email" -> com.java.baobaw.SharedRes.strings.to_signup_please_provide_your_email
        "signup requires a valid password" -> com.java.baobaw.SharedRes.strings.signup_requires_a_valid_password
        "user already registered" -> com.java.baobaw.SharedRes.strings.user_already_registered
        "only an email address or phone number should be provided on signup." -> com.java.baobaw.SharedRes.strings.only_an_email_address_or_phone_number_should_be_provided_on_signup
        "signups not allowed for this instance" -> com.java.baobaw.SharedRes.strings.signups_not_allowed_for_this_instance
        "email signups are disabled" -> com.java.baobaw.SharedRes.strings.email_signups_are_disabled
        "email link is invalid or has expired" -> com.java.baobaw.SharedRes.strings.email_link_is_invalid_or_has_expired
        "token has expired or is invalid" -> com.java.baobaw.SharedRes.strings.token_has_expired_or_is_invalid
        "the new email address provided is invalid" -> com.java.baobaw.SharedRes.strings.the_new_email_address_provided_is_invalid
        "password should be at least 6 characters" -> com.java.baobaw.SharedRes.strings.password_should_be_at_least_six_characters
        "invalid login credentials" -> com.java.baobaw.SharedRes.strings.invalid_login_credentials
        "verify requires either a token or a token hash" -> com.java.baobaw.SharedRes.strings.verify_requires_either_a_token_or_a_token_hash
        else -> { com.java.baobaw.SharedRes.strings.unexpected_error }
}