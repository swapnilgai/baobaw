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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
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
import com.java.cherrypick.android.compose.ccp.data.utils.checkPhoneNumber
import com.java.cherrypick.android.compose.passwordinput.PasswordInputField
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import com.java.cherrypick.presentationInfra.UiEvent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun EnterPhoneScreen(authViewModel: AuthViewModel = get()) {
    val phoneNumber = rememberSaveable { mutableStateOf("") }
    val fullPhoneNumber = rememberSaveable { mutableStateOf("") }
    val countryCodeNumber = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val confirmPassword = rememberSaveable { mutableStateOf("") }
    val checkNumberState = rememberSaveable { mutableStateOf(false) }

    BaseView(viewModel = authViewModel){
        val authContent = authViewModel.state.collectAsState()

        val scope = rememberCoroutineScope()
        val onDismissClicked :  () -> Unit =  { scope.launch { authViewModel.onDismissClicked() }}

        when(authContent.value){
            is UiEvent.Error -> {
                ErrorDialog(
                    onDismiss = onDismissClicked,
                    (authContent.value as UiEvent.Error).message
                )
            }
            is UiEvent.Loading -> {
                LoadingView(onDismiss = onDismissClicked)
            }
            else -> {
                // in case a value changes, we modify here to recompose that part only
            }
        }
    }

    CountryCodeView(
        phoneNumber,
        fullPhoneNumber,
        countryCodeNumber,
        password,
        confirmPassword,
        checkNumberState,
        onSignUpClick = authViewModel::onSignUpClick )
}


@Composable
fun CountryCodeView(
    phoneNumber: MutableState<String>,
    fullPhoneNumber: MutableState<String>,
    countryCodeNumber: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>,
    checkNumberState: MutableState<Boolean>,
    onSignUpClick: (String, String) -> Unit
){
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
        CountryCodePick(phoneNumber,
            fullPhoneNumber,
            countryCodeNumber,
            password,
            confirmPassword,
            checkNumberState,
            onSignUpClick)
    }
}


@Composable
fun CountryCodePick(
    phoneNumber: MutableState<String>,
    fullPhoneNumber: MutableState<String>,
    countryCodeNumber: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>,
    checkNumberState: MutableState<Boolean>,
    onSignUpClick: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CountryCodePicker(
            text = phoneNumber,
            countryCodeState = countryCodeNumber,
            onValueChange = {
                phoneNumber.value = it
                fullPhoneNumber.value = countryCodeNumber.value + it
                            },
            onCountryValueChange = {
                countryCodeNumber.value = it
                fullPhoneNumber.value = it + phoneNumber.value
            },
            bottomStyle = false,
            shape = RoundedCornerShape(1.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))


        if (checkNumberState.value && checkPhoneNumber(
                phone = phoneNumber.value, fullPhoneNumber = fullPhoneNumber.value, countryCode = countryCodeNumber.value
            )) Text(
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
            if (checkPhoneNumber(
                    phone = phoneNumber.value, fullPhoneNumber = fullPhoneNumber.value, countryCode = countryCodeNumber.value
                )
            ) {
                onSignUpClick(fullPhoneNumber.value, password.value)
            } else {
                checkNumberState.value = true
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