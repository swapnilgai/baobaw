package com.java.cherrypick.android.compose.moko.binder

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import dev.icerock.moko.geo.LocationTracker

@Suppress("FunctionNaming")
@Composable
fun BindLocationTrackerEffect(locationTracker: LocationTracker) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val context: Context = LocalContext.current

    LaunchedEffect(locationTracker, lifecycleOwner, context) {
        val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager

        locationTracker.bind(lifecycleOwner.lifecycle, context, fragmentManager)
    }
}

