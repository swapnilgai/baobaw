package com.java.cherrypick.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.java.cherrypick.Greeting
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.inject

class MainActivity : ComponentActivity() {
    val authViewModel: AuthViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GreetingView(Greeting().greet(), authViewModel)
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String, authViewModel: AuthViewModel) {
    Text(text = text)
    authViewModel.onSignUpClick()
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        //GreetingView("Hello, Android!")
    }
}
