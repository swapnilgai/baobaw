package com.java.cherrypick.android.compose.ccp.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Text
import androidx.compose.material.IconButton
import androidx.compose.material.Icon
import com.java.cherrypick.android.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.java.cherrypick.android.compose.ccp.data.utils.checkPhoneNumber
import com.java.cherrypick.android.compose.ccp.data.utils.getDefaultLangCode
import com.java.cherrypick.android.compose.ccp.data.utils.getDefaultPhoneCode
import com.java.cherrypick.android.compose.ccp.data.utils.getLibCountries
import com.java.cherrypick.android.compose.ccp.transformation.PhoneNumberTransformation


private var fullNumberState: String by mutableStateOf("")
private var checkNumberState: Boolean by mutableStateOf(false)
private var phoneNumberState: String by mutableStateOf("")
private var countryCodeState: String by mutableStateOf("")


@Composable
fun CountryCodePicker(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange: (String) -> Unit,
    shape: Shape = RoundedCornerShape(24.dp),
    showCountryCode: Boolean = true,
    bottomStyle: Boolean = false
) {
    val context = LocalContext.current
    var textFieldValue by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalTextInputService.current
    var phoneCode by rememberSaveable {
        mutableStateOf(
            getDefaultPhoneCode(
                context
            )
        )
    }
    var defaultLang by rememberSaveable {
        mutableStateOf(
            getDefaultLangCode(context)
        )
    }

    fullNumberState = phoneCode + textFieldValue
    phoneNumberState = textFieldValue
    countryCodeState = defaultLang

    Surface(color = colorResource(id = R.color.cream_white)) {
        Column(modifier = modifier) {
            if (bottomStyle) {
                CodeDialog(
                    pickedCountry = {
                        phoneCode = it.countryPhoneCode
                        defaultLang = it.countryCode
                    },
                    defaultSelectedCountry = getLibCountries.single { it.countryCode == defaultLang },
                    showCountryCode = showCountryCode,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(modifier = modifier.fillMaxWidth(),
                    shape = shape,
                    value = textFieldValue,
                    onValueChange = {
                        if (text != it && it.length  <= 10) {
                            textFieldValue = it
                            onValueChange(it)
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.cherry),
                        unfocusedBorderColor = colorResource(id = R.color.cherry),
                    ),

                    visualTransformation = PhoneNumberTransformation(getLibCountries.single { it.countryCode == defaultLang }.countryCode.uppercase()),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword,
                        autoCorrect = true,
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hideSoftwareKeyboard()
                    }),
                    leadingIcon = {
                        if (!bottomStyle)
                            Row {
                                Column {
                                    CodeDialog(
                                        pickedCountry = {
                                            phoneCode = it.countryPhoneCode
                                            defaultLang = it.countryCode
                                        },
                                        defaultSelectedCountry = getLibCountries.single { it.countryCode == defaultLang },
                                        showCountryCode = showCountryCode,
                                    )
                                }
                            }
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            textFieldValue = ""
                            onValueChange("")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }
        }
    }
}

fun getFullPhoneNumber(): String {
    return fullNumberState
}

fun getOnlyPhoneNumber(): String {
    return phoneNumberState
}

fun getErrorStatus(): Boolean {
    return !checkNumberState
}

fun isPhoneNumber(): Boolean {
    val check = checkPhoneNumber(
        phone = phoneNumberState, fullPhoneNumber = fullNumberState, countryCode = countryCodeState
    )
    checkNumberState = check
    return check
}