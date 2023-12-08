package com.java.baobaw.android.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.java.baobaw.SharedRes
import com.java.baobaw.android.BaseView
import com.java.baobaw.android.R
import com.java.baobaw.android.compose.ccp.component.CountryCodePicker
import com.java.baobaw.android.compose.ccp.component.getFullPhoneNumber
import com.java.baobaw.android.compose.passwordinput.PasswordInputField
import com.java.baobaw.feature.auth.presentation.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.java.baobaw.android.util.stringResource

@Composable
fun LoginScreen(loginViewModel: LoginViewModel,
                navController: NavController,
                scope: CoroutineScope = rememberCoroutineScope()){

    BaseView(viewModel = loginViewModel, navController = navController, scope = scope, setContentT = {}) {
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val phoneNumber = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }

            CountryCodePicker(
                text = phoneNumber.value,
                onValueChange = { phoneNumber.value = it },
                bottomStyle = false,
                shape = RoundedCornerShape(1.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            PasswordInputField(
                onValueChange = { it -> password.value = it },
            )
            Spacer(modifier = Modifier.padding(16.dp))

            Button(onClick = { scope.launch { loginViewModel.signIn(phoneNumber = getFullPhoneNumber(), password = password.value) }},
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
            ) {
                Text(text = stringResource(id = com.java.baobaw.SharedRes.strings.search))
            }

            Spacer(modifier = Modifier.padding(16.dp))

            ClickableText(text = AnnotatedString(stringResource(id = com.java.baobaw.SharedRes.strings.sign_up)), onClick = { scope.launch { loginViewModel.onSignUpClicked() } })

            Spacer(modifier = Modifier.padding(16.dp))

            ClickableText(text = AnnotatedString(stringResource(id = com.java.baobaw.SharedRes.strings.reset_password)), onClick = {scope.launch { loginViewModel.onResetPasswordClicked() }})

            Spacer(modifier = Modifier.padding(16.dp))

            ClickableText(text = AnnotatedString("Permissions"), onClick = {scope.launch { loginViewModel.onPermissionsClicked() }})

            Spacer(modifier = Modifier.padding(16.dp))

            ClickableText(text = AnnotatedString("Chat"), onClick = {scope.launch { loginViewModel.onChatClicked() }})
        }
    }
}