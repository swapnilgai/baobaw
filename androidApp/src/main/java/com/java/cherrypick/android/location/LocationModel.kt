package com.java.cherrypick.android.location

import com.google.android.gms.common.api.ResolvableApiException

data class GeoCoordinates(
    val latitude: Double,
    val longitude: Double
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val postalCode: String,
    val state: String,
    val country: String
)

data class LocationData(
    val zip: String,
    val formattedAddress: String
)

sealed class CheckLocationSettingResult {
    object LocationEnabled: CheckLocationSettingResult()

    data class LocationNotEnabled(
        val resolvableApiException: ResolvableApiException
    ): CheckLocationSettingResult()
}