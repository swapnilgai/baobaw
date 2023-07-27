package com.java.cherrypick.android.feature.location

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.java.cherrypick.SharedRes
import com.java.cherrypick.android.BaseView
import com.java.cherrypick.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.java.cherrypick.android.util.stringResource
import com.java.cherrypick.feature.auth.presentation.LocationContent
import com.java.cherrypick.feature.auth.presentation.LocationViewModel
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController

@Composable
fun UserLocationScreen(locationViewModel: LocationViewModel,
                navController: NavController,
                scope: CoroutineScope = rememberCoroutineScope()){

    var verifyUserState by remember { mutableStateOf<LocationContent?>(null) }

    fun setContent(state: LocationContent){
        verifyUserState = state
    }

    val locationTracker = LocationTracker(
        permissionsController = PermissionsController(applicationContext = LocalContext.current)
    )

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val context: Context = LocalContext.current

    LaunchedEffect(locationTracker, lifecycleOwner, context) {
        val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager

        locationTracker.bind(lifecycleOwner.lifecycle, context, fragmentManager)
    }

    BaseView(viewModel = locationViewModel, navController = navController, setContentT = {state -> setContent(state)}) {
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(onClick = { scope.launch { locationViewModel.getCurrentLocation(locationTracker) }},
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
            ) {
                Text(text = "Get Location")
            }

            Spacer(modifier = Modifier.padding(16.dp))

            Text(
                text = verifyUserState?.lat.toString(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp),
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Text(
                text = verifyUserState?.lan.toString(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp),
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}