package com.java.cherrypick.android.location

import android.Manifest
import com.java.cherrypick.presentationInfra.BaseViewModel
import java.security.Permission

data class LocationContent(
    val locationSearchResult: List<LocationData>? = emptyList(),
    val title: String
)

class LocationViewModel(val locationInteractor: LocationInteractor): BaseViewModel<LocationContent>(LocationContent(title = "Enter City name or zip")) {

    private val requestCodeSend: Int = 100

    fun onCurrentLocationClicked(){
        if(!locationInteractor.isLocationPermissionGranted())
            requestPermission(LocationPermission.LOCATION.permissionString, requestCodeSend)
    }
}


enum class LocationPermission(vararg permissionString: String){
    LOCATION(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    );

    val permissionString: List<String> = permissionString.toList()
}