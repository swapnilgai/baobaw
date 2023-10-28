package com.java.baobaw.android

import android.util.Log
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.java.baobaw.SharedRes
import com.java.baobaw.android.navigation.navigateToScreen
import com.java.baobaw.presentationInfra.BaseViewModel
import com.java.baobaw.presentationInfra.UiEvent
import com.java.baobaw.android.util.stringResource


@Composable
fun <ContentT>BaseView(
    viewModel: BaseViewModel<ContentT>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    navController: NavController,
    setContentT: (ContentT) -> Unit,
    content: @Composable () -> Unit) {

    content.invoke()
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.onStart()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.clear()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val viewState = viewModel.state.collectAsStateWithLifecycle()

    when (viewState.value) {
        is UiEvent.Content -> {
                setContentT((viewState.value as UiEvent.Content<out ContentT>).value)
        }
        is UiEvent.Error -> {
            ErrorDialog(
                onDismiss = { viewModel.onDismiss() },
                stringResource((viewState.value as UiEvent.Error).message)
            )
        }
        is UiEvent.Loading -> {
             LoadingView(onDismiss = { viewModel.onDismiss() })
        }
        is UiEvent.Navigation -> {
                navigateToScreen(navController, (viewState.value as UiEvent.Navigation).route)
        }
        else -> {
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
            { Text(text = stringResource(id = com.java.baobaw.SharedRes.strings.ok)) }
        },
        title = { Text(text = stringResource(id = com.java.baobaw.SharedRes.strings.error)) },
        text = { Text(text = message) }
    )
}


fun <T> stateSaver() = Saver<MutableState<T>, Any>(
    save = { state -> state.value ?: "null" },
    restore = { value ->
        @Suppress("UNCHECKED_CAST")
        mutableStateOf((if (value == "null") null else value) as T)
    }
)

