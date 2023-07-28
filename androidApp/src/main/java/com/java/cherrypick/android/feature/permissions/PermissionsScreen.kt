package com.java.cherrypick.android.feature.permissions

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.java.cherrypick.android.BaseView
import com.java.cherrypick.android.R
import com.java.cherrypick.android.compose.moko.binder.BindLocationTrackerEffect
import com.java.cherrypick.android.compose.moko.binder.LocationTrackerAccuracy
import com.java.cherrypick.android.compose.moko.binder.LocationTrackerFactory
import com.java.cherrypick.android.compose.moko.binder.rememberLocationTrackerFactory
import com.java.cherrypick.feature.auth.presentation.PermissionContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.java.cherrypick.feature.auth.presentation.PermissionViewModel
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory

@Composable
fun PermissionsScreen(permissionViewModel: PermissionViewModel,
                       navController: NavController,
                       scope: CoroutineScope = rememberCoroutineScope()){

    var viewState by remember { mutableStateOf<PermissionContent?>(null) }

    fun setContent(state: PermissionContent){
        viewState = state
    }

    BaseView(viewModel = permissionViewModel, navController = navController, setContentT = {state -> setContent(state)}) {
        val permissionsControllerFactory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
        val controller: PermissionsController = remember(permissionsControllerFactory) { permissionsControllerFactory.createPermissionsController() }

        val locationTrackerFactory : LocationTrackerFactory =  rememberLocationTrackerFactory(LocationTrackerAccuracy.Best)
        val locationTracker = locationTrackerFactory.createLocationTracker(controller)

        BindLocationTrackerEffect(locationTracker)
        BindEffect(controller)

        Column(
            Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(onClick = { scope.launch { permissionViewModel.requestPermission(Permission.LOCATION, controller) }},
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
            ) {
                Text(text = "Location Permission")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = { scope.launch { permissionViewModel.requestPermission(Permission.GALLERY, controller) }},
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
            ) {
                Text(text = "Gallery")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = { scope.launch { permissionViewModel.getCurrentLocation(locationTracker) }},
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
            ) {
                Text(text = "Get Location")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = viewState?.locationContent.toString(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp),
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = viewState?.permissionState.toString(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp),
                fontSize = 18.sp,
                color = Color.Black
            )

        }
    }
}