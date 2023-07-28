package com.java.cherrypick.feature.auth.presentation

import com.java.cherrypick.SharedRes
import com.java.cherrypick.presentationInfra.BaseViewModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.launch


class PermissionViewModel: BaseViewModel<PermissionState>(initialContent = PermissionState.NotDetermined) {

    fun requestPermission(permission: Permission, permissionsController: PermissionsController) {
        viewModelScope.launch {
            try {
                setLoading()
                permissionsController.providePermission(permission)
                val stat = permissionsController.getPermissionState(permission)
                setContent { stat }
            } catch (deniedAlwaysException: DeniedAlwaysException) {
                setError(message = SharedRes.strings.error)
            } catch (deniedException: DeniedException) {
                setError(message = SharedRes.strings.error)
            } finally {
            }
        }
    }
}