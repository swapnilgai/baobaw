package com.java.cherrypick.android.feature.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.java.cherrypick.SharedRes
import com.java.cherrypick.android.BaseView
import com.java.cherrypick.android.R
import com.java.cherrypick.feature.auth.presentation.VerifyUserState
import com.java.cherrypick.feature.auth.presentation.VerifyUserViewModel
import kotlinx.coroutines.launch
import com.java.cherrypick.android.util.stringResource

@Composable
fun VerifyOtpScreen(
    verifyUserViewModel: VerifyUserViewModel,
    phoneNumber: String,
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavController){

    var authState by remember { mutableStateOf<VerifyUserState?>(null) }

    fun setAuthState(state: VerifyUserState){
        authState = state
    }

    BaseView(viewModel = verifyUserViewModel, navController = navController, setContentT = { state -> setAuthState(state)}) {
        VerifyOtpView(phoneNumber = phoneNumber,
            onSendClicked = { phoneNumber, opt -> verifyUserViewModel.verifyOpt(phoneNumber, opt) },
            scope = scope
        )
    }
}

@Composable
fun VerifyOtpView(
    phoneNumber: String,
    scope: CoroutineScope,
    onSendClicked: (String, String) -> Unit
){
        var otpValue by remember { mutableStateOf("") }
        Column {
            OtpTextField(
                otpText = otpValue,
                onOtpTextChange = { value, _ ->
                    otpValue = value
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        onSendClicked.invoke(phoneNumber, otpValue)
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
            ) {
                Text(text = stringResource(id =  SharedRes.strings.send))
            }
        }
}

@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
        }
    }

    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text, it.text.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        text = otpText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> ""
        index > text.length -> ""
        else -> text[index].toString()
    }
    Text(
        modifier = Modifier
            .width(40.dp)
            .border(
                1.dp, when {
                    isFocused -> colorResource(id = R.color.cherry)
                    else -> colorResource(id = R.color.cherry)
                }, RoundedCornerShape(8.dp)
            )
            .padding(2.dp),
        text = char,
        style = MaterialTheme.typography.h4,
        color = if (isFocused) {
            colorResource(id = R.color.cherry)
        } else {
            colorResource(id = R.color.cherry)
        },
        textAlign = TextAlign.Center
    )
}