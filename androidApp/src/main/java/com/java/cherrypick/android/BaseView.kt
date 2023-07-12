package com.java.cherrypick.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.java.cherrypick.presentationInfra.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel


@Composable
fun <ContentT>BaseView(viewModel: BaseViewModel<ContentT>,
                       lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
                       content: @Composable () -> Unit) {

    content.invoke()
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.onStart()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.clear()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


@Composable
fun LoadingView(onDismiss:() -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier,
            elevation = 8.dp
        ) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 32.dp, bottom = 32.dp, start = 64.dp, end = 64.dp)
                )
            }
        }
    }
}


@Composable
fun ErrorDialog(onDismiss:() -> Unit , message: String) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = onDismiss)
            { Text(text = stringResource(id = R.string.ok)) }
        },
        title = { Text(text = stringResource(id = R.string.error)) },
        text = { Text(text = message) }
    )
}

