package com.java.baobaw.android.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.java.baobaw.android.feature.auth.EnterPhoneScreen
import com.java.baobaw.android.feature.auth.LoginScreen
import com.java.baobaw.android.feature.auth.ResetPasswordScreen
import com.java.baobaw.android.feature.auth.VerifyOtpScreen
import com.java.baobaw.android.feature.chat.ChatScreen
import com.java.baobaw.android.feature.permissions.PermissionsScreen
import com.java.baobaw.android.feature.photo_picker.PhotoPickerScreen
import com.java.baobaw.android.feature.userinput.UserInputScreen
import com.java.baobaw.util.getNavigationUrl
import org.koin.androidx.compose.get

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.Login.route) {
        composable(route = Screens.Login.route){
            LoginScreen(loginViewModel = get(), navController = navController)
        }
        composable(route = Screens.SignUp.route){
            EnterPhoneScreen(authViewModel = get(), navController)
        }
        composable(route = getNavigationUrl(baseRoute = Screens.VerifyOpt.route, listOf(com.java.baobaw.AppConstants.NavigationParam.PHONE_NUMBER, com.java.baobaw.AppConstants.NavigationParam.SEND_OPT)),
                arguments = listOf(navArgument(com.java.baobaw.AppConstants.NavigationParam.PHONE_NUMBER){
                type = NavType.StringType
            }, navArgument(com.java.baobaw.AppConstants.NavigationParam.SEND_OPT){
                    type = NavType.BoolType
                }
        )){ navBackStackEntry ->
            val phoneNumber = navBackStackEntry.arguments?.getString(com.java.baobaw.AppConstants.NavigationParam.PHONE_NUMBER)?: ""
            val sendOpt = navBackStackEntry.arguments?.getBoolean(com.java.baobaw.AppConstants.NavigationParam.SEND_OPT)?: false
            VerifyOtpScreen(verifyUserViewModel = get(), phoneNumber = phoneNumber, sendOpt = sendOpt, navController = navController)
        }
        composable(route = Screens.UserInput.route){
            UserInputScreen()
        }
        composable(route = Screens.ResetPassword.route){
            ResetPasswordScreen(resetPasswordViewModel = get(), navController = navController)
        }
        composable(route = Screens.PermissionsScreen.route){
            PermissionsScreen(permissionViewModel = get(), navController = navController)
        }
        composable(route = Screens.ImagePickerScreen.route){
            PhotoPickerScreen(imageSelectionViewModel = get(), navController = navController)
        }
        composable(route = Screens.ChatScreen.route){
            ChatScreen(chatViewModel = get(), navController = navController)
        }
    }
}

@Composable
fun navigateToScreen(navController: NavController, route: String){
    LaunchedEffect(route){
        navController.navigate(route)
    }
}


