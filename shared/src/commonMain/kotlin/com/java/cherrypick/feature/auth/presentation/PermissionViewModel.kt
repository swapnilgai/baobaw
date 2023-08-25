package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.AppConstants.RoutIds.imagePickerScreen
import com.java.cherrypick.feature.location.interactor.LocationInteractor
import com.java.cherrypick.interactor.interactorLaunch
import com.java.cherrypick.presentationInfra.BaseViewModel
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class LocationContent(val lat: Double = 0.0, val lan: Double = 0.0)

data class PermissionContent(val permissionState: PermissionState = PermissionState.NotDetermined, val locationContent: LocationContent?=null)

class PermissionViewModel(val locationInteractor: LocationInteractor): BaseViewModel<PermissionContent>(initialContent = PermissionContent()) {

    fun requestPermission(permission: Permission, permissionsController: PermissionsController) {
        viewModelScope.interactorLaunch {
            try {
                setLoading()
                permissionsController.providePermission(permission)
                val stat = permissionsController.getPermissionState(permission)
                setContent { copy(permissionState = stat)}
            } catch (deniedAlwaysException: DeniedAlwaysException) {
                setContent { copy(permissionState = PermissionState.Denied)}
            } catch (deniedException: DeniedException) {
                setContent { copy(permissionState = PermissionState.DeniedAlways)}
            }
        }
    }

    fun getCurrentLocation(locationTracker: LocationTracker) {
        viewModelScope.interactorLaunch {
            try {
                setLoading()
                locationTracker.startTracking()
                val result = locationTracker.getLocationsFlow().first()
                setContent { copy( locationContent = LocationContent(lat = result.latitude, lan = result.longitude) ) }
                locationTracker.stopTracking()
            } catch (deniedAlwaysException: DeniedAlwaysException) {
                setContent { copy(permissionState = PermissionState.Denied)}
            } catch (deniedException: DeniedException) {
                setContent { copy(permissionState = PermissionState.DeniedAlways)}
            }
        }
    }

    fun onImagePickerClicked(){
        navigate(imagePickerScreen)
    }

    fun updateLocation(locationTracker: LocationTracker){
        viewModelScope.interactorLaunch {
            try {
                setLoading()
                locationTracker.startTracking()
                val result = locationTracker.getLocationsFlow().first()
                locationTracker.stopTracking()
                locationInteractor.updateLocation(result.latitude.toString(), result.longitude.toString())
                setContent { copy( locationContent = LocationContent(lat = result.latitude, lan = result.longitude) ) }
            } catch (deniedAlwaysException: DeniedAlwaysException) {
                setContent { copy(permissionState = PermissionState.Denied)}
            } catch (deniedException: DeniedException) {
                setContent { copy(permissionState = PermissionState.DeniedAlways)}
            }
        }

    }
}