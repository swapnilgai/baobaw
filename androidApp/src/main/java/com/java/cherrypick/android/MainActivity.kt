package com.java.cherrypick.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.java.cherrypick.android.feature.auth.EnterPhoneScreen
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.cream_white)
                ) {
                    val authViewModel: AuthViewModel by inject()
                    EnterPhoneScreen(authViewModel = authViewModel)
                }
            }
        }
    }
}
@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        //EnterPhoneScreen()
    }
}
