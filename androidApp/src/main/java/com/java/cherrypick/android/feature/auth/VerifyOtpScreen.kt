package com.java.cherrypick.android.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.java.cherrypick.AppConstants.Auth.otpCount
import com.java.cherrypick.android.BaseView
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun VerifyOtpScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    phoneNumber: String
) {

    BaseView(viewModel = authViewModel) {

        val otpText = rememberSaveable { mutableStateOf(phoneNumber) }
        val scope = rememberCoroutineScope()
        val onOptClicked: (String, String) -> Unit = { phone, opt ->
            scope.launch {
                authViewModel.verifyOpt(
                    phoneNumber = phone,
                    opt = opt
                )
            }
        }

        BasicTextField(
            modifier = modifier,
            value = TextFieldValue(otpText.value, selection = TextRange(otpText.value.length)),
            onValueChange = {
                otpText.value = it.text
                if (it.text.length == otpCount) {
                    onOptClicked(it.text, phoneNumber)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(otpCount) { index ->
                        CharView(
                            index = index,
                            text = otpText.value
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        )
    }
}

@Composable
private fun CharView(
    index: Int,
    text: String
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> "0"
        index > text.length -> ""
        else -> text[index].toString()
    }
    Text(
        modifier = Modifier
            .width(40.dp)
            .padding(2.dp),
        text = char,
        style = MaterialTheme.typography.h4,
        textAlign = TextAlign.Center
    )
}