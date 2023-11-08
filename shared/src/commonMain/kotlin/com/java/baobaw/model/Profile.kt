package com.java.baobaw.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val user_id: String,
    val first_name: String,
    val last_name: String,
    val birthdate: String,
    val birthtime: String,
    val bio: String,
    val interests: List<String>,
    val gender: String,
    val relationship_status: String,
    val education: String,
    val occupation: String,
    val allow_search: Boolean,
    val timezone: String,
    val birth_lat: Double,
    val birth_lon: Double,
    val image_url_one: String,
    val image_url_two: String,
    val image_url_three: String
)

