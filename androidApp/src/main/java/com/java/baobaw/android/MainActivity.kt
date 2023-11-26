package com.java.baobaw.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.java.baobaw.android.navigation.NavigationGraph
import com.java.baobaw.feature.common.presentation.MainViewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    val mainViewModel by inject<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //mainViewModel.initCompatibilityBatchInBackground()

        setContent {
            val navController = rememberNavController()
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.cream_white)
                ) {
                    NavigationGraph(navController = navController)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.cancelSubscribeToChat()
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        NavigationGraph(rememberNavController())
    }
}
