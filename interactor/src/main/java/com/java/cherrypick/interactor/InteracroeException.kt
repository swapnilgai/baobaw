package com.java.cherrypick.interactor

import java.net.HttpRetryException

sealed class InteracroeException : Exception() {
    abstract override val message: String
    sealed class HTTP: InteracroeException() {
        data class ServerError internal constructor(override val message: String): HTTP()

        data class Unauthorize internal constructor(override val message: String): HTTP()
    }
    data class Generic constructor(override val message: String): InteracroeException()
}

//TODO update exception handling
fun Exception.toInteractorException(): InteracroeException {
    return when(this){
        is HttpRetryException -> toInteractorException(this.responseCode())
        else -> InteracroeException.Generic(message = this.message?: "Something wen wrong")
    }
}

//TODO add this strings to string.xml
fun toInteractorException(code: Int): InteracroeException =
    when(code){
        500, 503 ->  InteracroeException.HTTP.ServerError("Internal Server error")
        401 ->  InteracroeException.HTTP.Unauthorize("Your not authorized")
        else -> InteracroeException.Generic("Something wen wrong")
    }
