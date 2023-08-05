package com.java.cherrypick.android.feature.photo_picker

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.java.cherrypick.SharedRes
import com.java.cherrypick.android.BaseView
import com.java.cherrypick.android.R
import com.java.cherrypick.android.util.stringResource
import com.java.cherrypick.feature.auth.presentation.PermissionContent
import com.java.cherrypick.feature.auth.presentation.PermissionViewModel
import com.java.cherrypick.feature.upload.presentation.ImageSelectionContent
import com.java.cherrypick.feature.upload.presentation.ImageSelectionViewModel
import dev.icerock.moko.media.compose.BindMediaPickerEffect
import dev.icerock.moko.media.compose.rememberMediaPickerControllerFactory
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PhotoPickerScreen(imageSelectionViewModel: ImageSelectionViewModel,
                      navController: NavController,
                      scope: CoroutineScope = rememberCoroutineScope()) {

    var viewState by remember { mutableStateOf<ImageSelectionContent?>(null) }

    fun setContent(state: ImageSelectionContent){
        viewState = state
    }

    BaseView(viewModel = imageSelectionViewModel, navController = navController, setContentT = {state -> setContent(state)}) {

        val permissionsControllerFactory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
        val controller: PermissionsController = remember(permissionsControllerFactory) { permissionsControllerFactory.createPermissionsController() }

        val factory = rememberMediaPickerControllerFactory()
        val picker = remember(factory) { factory.createMediaPickerController(controller) }

        BindEffect(controller)
        BindMediaPickerEffect(picker)

        Column(
            Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        )  {

            Button(onClick = { scope.launch { imageSelectionViewModel.onGalleryPressed(picker) }},
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
            ) {
                Text(text = "Gallery")
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Button(onClick = { scope.launch { imageSelectionViewModel.uploadImage(viewState?.data!!) }},
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
            ) {
                Text(text = "Upload")
            }
            Spacer(modifier = Modifier.padding(5.dp))
            viewState?.data?.let {
                BitmapImage(bitmap = it.platformBitmap)
            }
        }
    }
}


@Composable
fun BitmapImage(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "some useful description",
    )
}