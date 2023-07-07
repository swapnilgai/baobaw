package com.java.cherrypick.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.java.cherrypick.android.feature.auth.EnterPhoneScreen
import org.koin.androidx.compose.get

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SignUp.route) {
        composable(route = Routes.SignUp.route){
            EnterPhoneScreen(authViewModel = get())
        }
        composable(route = Routes.VerifyOpt.route){

        }
    }
}