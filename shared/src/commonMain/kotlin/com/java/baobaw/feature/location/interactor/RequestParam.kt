package com.java.baobaw.feature.location.interactor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(@SerialName("user_id") val userId: String ,@SerialName("location") val location: String, @SerialName("allow_search") val allowSearch: Boolean)

@Serializable
data class Point(val lat: String, val lan: String){
    override fun toString(): String {
        return "POINT($lat $lan)"
    }
}


