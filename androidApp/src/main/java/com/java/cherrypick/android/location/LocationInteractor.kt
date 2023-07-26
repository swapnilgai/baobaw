package com.java.cherrypick.android.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.java.cherrypick.interactor.Interactor
import com.java.cherrypick.interactor.withInteractorContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume


interface LocationInteractor {

    suspend fun getCurrentGeoCoordinates(): GeoCoordinates?

    suspend fun getAndSaveCurrentLocation()

    suspend fun saveLocation(userLocation: UserLocation)

    suspend fun getLocationPrediction(query: String): List<LocationData>

    suspend fun getGeoCodedLocation(zipCode: String): UserLocation

    fun isLocationPermissionGranted(): Boolean

    suspend fun checkLocationSettings(): CheckLocationSettingResult
}

class LocationInteractorImpl(val context: Context): LocationInteractor, Interactor {

    private val locationRequest
        get() = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(context) }
    override suspend fun getCurrentGeoCoordinates(): GeoCoordinates? = withInteractorContext {
        try {
            val location = getUserCurrentLocation()
            GeoCoordinates(
                latitude = location.latitude,
                longitude = location.longitude
            )
        } catch (e: Exception){
            null
        }
    }

    override suspend fun getAndSaveCurrentLocation() {
        TODO("Not yet implemented")
    }

    override suspend fun saveLocation(userLocation: UserLocation) {
        TODO("Not yet implemented")
    }

    override suspend fun getLocationPrediction(query: String): List<LocationData> {
        TODO("Not yet implemented")
    }

    override suspend fun getGeoCodedLocation(zipCode: String): UserLocation {
        TODO("Not yet implemented")
    }

    override fun isLocationPermissionGranted(): Boolean{
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun checkLocationSettings(): CheckLocationSettingResult {
        TODO("Not yet implemented")
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
         if(isLocationPermissionGranted()) {
             val looper = Looper.myLooper() ?: Looper.getMainLooper()
             fusedLocationProviderClient.requestLocationUpdates(
                 locationRequest,
                 locationCallBack,
                 looper
             )
         }
     } catch (exception: Exception){
         if (continuation.isActive){
             continuation.resumeWithException(exception)
             stopUpdatingLocation(locationCallBack)
         }
     }
 }


 private fun stopUpdatingLocation(locationCallback: LocationCallback){
     fusedLocationProviderClient.removeLocationUpdates(locationCallback)
 }


    private fun CancellableContinuation<Location>.resumeLatLocation(exception: Exception? = null) {
        try {
            fusedLocationProviderClient.lastLocation
        } catch (e: SecurityException) {
            null
        }?.addOnCompleteListener { task ->
            if (isActive) {
                if (task.isCanceled) {
                    task.result
                        ?.let { location -> resume(location) }
                        ?: resumeWithException(exception ?: LocationException(LocationError.LOCATION_NOT_AVAILABLE))
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


class LocationException(val error: LocationError): Exception(error.name)