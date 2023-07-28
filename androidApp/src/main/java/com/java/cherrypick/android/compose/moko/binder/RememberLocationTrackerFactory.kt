package com.java.cherrypick.android.compose.moko.binder

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.Priority
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController

interface LocationTrackerFactory {
    fun createLocationTracker(): LocationTracker
    fun createLocationTracker(permissionsController: PermissionsController): LocationTracker
}

enum class LocationTrackerAccuracy {
    Best,
    Medium,
    LowPower
}

@Composable
fun rememberLocationTrackerFactory(accuracy: LocationTrackerAccuracy): LocationTrackerFactory {
    val context: Context = LocalContext.current
    return remember(context) {
        object : LocationTrackerFactory {
            override fun createLocationTracker(): LocationTracker {
                return LocationTracker(
                    permissionsController = PermissionsController(
                        applicationContext = context.applicationContext
                    ),
                    priority = accuracy.toPriority()
                )
            }

            override fun createLocationTracker(
                permissionsController: PermissionsController
            ): LocationTracker {
                return LocationTracker(
                    permissionsController = permissionsController,
                    priority = accuracy.toPriority()
                )
            }
        }
    }
}

private fun LocationTrackerAccuracy.toPriority(): Int {
    return when (this) {
        LocationTrackerAccuracy.Best -> Priority.PRIORITY_HIGH_ACCURACY
        LocationTrackerAccuracy.Medium -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
        LocationTrackerAccuracy.LowPower -> Priority.PRIORITY_LOW_POWER
    }
}