package com.java.cherrypick.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.java.cherrypick.android.feature.auth.EnterPhoneScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.cream_white)
                ) {
                    EnterPhoneScreen()
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
