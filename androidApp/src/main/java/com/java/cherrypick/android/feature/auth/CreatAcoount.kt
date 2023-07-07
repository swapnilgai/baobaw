package com.java.cherrypick.android.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.java.cherrypick.android.BaseView
import com.java.cherrypick.android.ErrorDialog
import com.java.cherrypick.android.LoadingView
import com.java.cherrypick.android.R
import com.java.cherrypick.android.compose.ccp.component.CountryCodePicker
import com.java.cherrypick.android.compose.ccp.component.getErrorStatus
import com.java.cherrypick.android.compose.ccp.component.getFullPhoneNumber
import com.java.cherrypick.android.compose.ccp.component.getOnlyPhoneNumber
import com.java.cherrypick.android.compose.ccp.component.isPhoneNumber
import com.java.cherrypick.android.compose.passwordinput.PasswordInputField
import com.java.cherrypick.feature.auth.presentation.AuthState
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import com.java.cherrypick.model.ErrorMessage
import com.java.cherrypick.presentationInfra.UiEvent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun EnterPhoneScreen(authViewModel: AuthViewModel = get()) {
    BaseView(viewModel = authViewModel){
        val authContent = remember { mutableStateOf(AuthState()) }
        val scope = rememberCoroutineScope()
        val onSignUpClick :  (String, String) -> Unit =  {  phone, password -> scope.launch { authViewModel.onSignUpClick(phone, password) }}
        val onDismissClicked :  () -> Unit =  { scope.launch { authViewModel.onDismissClicked() }}
        LaunchedEffect(authContent){
                for (event in authViewModel.uiChannel.observe(this)) {
                    when(event) {
                        is UiEvent.Content -> {
                            authContent.value = event.value
                        }
                        is UiEvent.Loading -> { authContent.value = authContent.value.copy(showLoading =  true) }
                        is UiEvent.Error -> { authContent.value = authContent.value.copy(showLoading =  false, errorMessage = ErrorMessage(message = event.message)) }
                        else -> {}
                    }
                }
        }

        CountryCodeView( onSignUpClick = onSignUpClick )

        if(authContent.value.showLoading) LoadingView(onDismiss = onDismissClicked)

        authContent.value.errorMessage?.message?.let { ErrorDialog(onDismiss =  onDismissClicked , it) }
    }
}


@Composable
fun CountryCodeView(onSignUpClick: (String, String) -> Unit){
    Column(
        Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.enter_phone_number),
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )
        CountryCodePick(onSignUpClick)
    }
}


@Composable
fun CountryCodePick(onSignUpClick: (String, String) -> Unit) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val phoneNumber = rememberSaveable { mutableStateOf("") }
        val fullPhoneNumber = rememberSaveable { mutableStateOf("") }
        val onlyPhoneNumber = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        val confirmPassword = rememberSaveable { mutableStateOf("") }

        CountryCodePicker(
            text = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            bottomStyle = false,
            shape = RoundedCornerShape(1.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))


        if (getErrorStatus() && isPhoneNumber()) Text(
            text = stringResource(id = R.string.invalid_number),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        PasswordInputField(onValueChange = {it -> password.value = it }, modifier = Modifier.padding(start = 16.dp, end = 16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        PasswordInputField(onValueChange = {it -> confirmPassword.value = it }, modifier = Modifier.padding(start = 16.dp, end = 16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (isPhoneNumber()) {
                fullPhoneNumber.value = getFullPhoneNumber()
                onlyPhoneNumber.value = getOnlyPhoneNumber()
                onSignUpClick(fullPhoneNumber.value, password.value)
            } else {
                fullPhoneNumber.value = "Error"
                onlyPhoneNumber.value = "Error"
            }
        },
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
        ) {
            Text(text = stringResource(id = R.string.send))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    //EnterPhoneScreen()
}