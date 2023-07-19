package com.java.cherrypick.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.java.cherrypick.AppConstants
import com.java.cherrypick.android.feature.auth.EnterPhoneScreen
import com.java.cherrypick.android.feature.auth.LoginScreen
import com.java.cherrypick.android.feature.auth.ResetPasswordScreen
import com.java.cherrypick.android.feature.auth.VerifyOtpScreen
import com.java.cherrypick.android.feature.userinput.UserInputScreen
import com.java.cherrypick.util.getNavigationUrl
import org.koin.androidx.compose.get

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.Login.route) {
        composable(route = Screens.Login.route){
            LoginScreen(loginViewModel = get(), navController = navController)
        }
        composable(route = Screens.SignUp.route){
            EnterPhoneScreen(authViewModel = get(), navController)
        }
        composable(route = getNavigationUrl(baseRoute = Screens.VerifyOpt.route, listOf(AppConstants.NavigationParam.phoneNumber)),
                arguments = listOf( navArgument(AppConstants.NavigationParam.phoneNumber){
                type = NavType.StringType
            }
        )){ navBackStackEntry ->
            val phoneNumber = navBackStackEntry.arguments?.getString(AppConstants.NavigationParam.phoneNumber)?: ""
            VerifyOtpScreen(verifyUserViewModel = get(), phoneNumber = phoneNumber, navController = navController)
        }
        composable(route = Screens.UserInput.route){
            UserInputScreen()
        }
        composable(route = Screens.ResetPassword.route){
            ResetPasswordScreen(resetPasswordViewModel = get(), navController = navController)
        }
    }
}

@Composable
fun navigateToScreen(navController: NavController, route: String){
    LaunchedEffect(route){
        navController.navigate(route)
    }
}


