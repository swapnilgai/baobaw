package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.presentationInfra.BaseViewModel
import dev.icerock.moko.geo.LocationTracker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class LocationContent(val lat: Double = 0.0, val lan: Double = 0.0)

class LocationViewModel: BaseViewModel<LocationContent>(initialContent = LocationContent()) {

    fun getCurrentLocation(locationTracker: LocationTracker) {
        viewModelScope.launch {
            setLoading()
            locationTracker.startTracking()
            val result = locationTracker.getLocationsFlow().first()
            setContent { copy(lat = result.latitude, lan = result.longitude ) }
            locationTracker.stopTracking()
        }
    }
}
