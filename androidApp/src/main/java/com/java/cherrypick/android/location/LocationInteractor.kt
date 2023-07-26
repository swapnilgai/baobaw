package com.java.cherrypick.android.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.java.cherrypick.interactor.Interactor
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


interface LocationInteractor {

    suspend fun getCurrentLocation(): GeoCoordinates?

    suspend fun getAndSaveCurrentLocation()

    suspend fun saveLocation(userLocation: UserLocation)

    suspend fun getLocationPrediction(query: String): List<LocationData>

    suspend fun getGeoCodedLocation(zipCode: String): UserLocation

    fun isLocationPermissionGranted(): Boolean

    suspend fun checkLocationSettings(): CheckLocationSettingResult
}

class LocationInteractorImpl(val context: Context):  Interactor {

    private val locationRequest
        get() = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(context) }


    fun isLocationPermissionGranted(): Boolean{
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


 private suspend fun getUserCurrentLocation(): Location = suspendCancellableCoroutine { continuation ->
     val locationCallBack = object : LocationCallback(){
         override fun onLocationResult(locationResult: LocationResult) {
             if(continuation.isActive){
                 locationResult.locations.getOrNull(0)
                     ?.let { location -> continuation.resume(location) }
                     ?: continuation.resumeWithException(LocationException(LocationError.LOCATION_NOT_AVAILABLE))

                 stopUpdatingLocation(this)
             }
         }

         override fun onLocationAvailability(locationAvailability: LocationAvailability) {
             if(continuation.isActive && !locationAvailability.isLocationAvailable){
                 continuation.resumeLatLocation()
             }
         }
     }

     try {
         val looper = Looper.myLooper() ?: Looper.getMainLooper()
         fusedLocationProviderClient.requestLocationUpdates(
             locationRequest,
             locationCallBack,
             looper
         )
     } catch (e: Exception){
         if (continuation.isActive){
             continuation.resumeWithException(e)
             stopUpdatingLocation(locationCallBack)
         }
     }
 }


 private fun stopUpdatingLocation(locationCallback: LocationCallback){
     fusedLocationProviderClient.removeLocationUpdates(locationCallback)
 }


    private fun <T> CancellableContinuation<T>.resumeLatLocation(exception: Exception? = null) {
        try {
            fusedLocationProviderClient.lastLocation
        } catch (e: SecurityException) {
            null
        }?.addOnCompleteListener { task ->
            if (isActive) {
                if (task.isCanceled) {
                    task.result
                        ?.let { location -> resume(location) }
                        ?: resumeWithException(
                            exception ?: LocationException(LocationError.LOCATION_NOT_AVAILABLE)
                        )
                } else {
                    resumeWithException(exception ?: LocationException(LocationError.LOCATION_NOT_AVAILABLE))
                }
            }
        }?:  resumeWithException(exception ?: LocationException(LocationError.LOCATION_NOT_AVAILABLE))
    }

}


enum class LocationError{
    LOCALITY_NULL,
    COUNTRY_NAME_NULL,
    LOCATION_NOT_AVAILABLE,
    NO_ADDRESS_RETURNED_BY_GEO_CODER
}


class LocationException(val error: LocationError): Exception(error(error.name))