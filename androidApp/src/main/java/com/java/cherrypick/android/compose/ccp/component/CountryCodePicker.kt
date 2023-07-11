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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Icon
import com.java.cherrypick.android.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.java.cherrypick.android.compose.ccp.data.utils.getDefaultLangCode
import com.java.cherrypick.android.compose.ccp.data.utils.getDefaultPhoneCode
import com.java.cherrypick.android.compose.ccp.data.utils.getLibCountries
import com.java.cherrypick.android.compose.ccp.transformation.PhoneNumberTransformation


@Composable
fun CountryCodePicker(
    modifier: Modifier = Modifier,
    text: MutableState<String>,
    countryCodeState: MutableState<String>,
    onValueChange: (String) -> Unit,
    onCountryValueChange: (String) -> Unit,
    shape: Shape = RoundedCornerShape(24.dp),
    showCountryCode: Boolean = true,
    bottomStyle: Boolean = false
) {
    val context = LocalContext.current
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

    countryCodeState.value = phoneCode

    Surface(color = colorResource(id = R.color.cream_white)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            if (bottomStyle) {
                CodeDialog(
                    pickedCountry = {
                        phoneCode = it.countryPhoneCode
                        defaultLang = it.countryCode
                        onCountryValueChange(it.countryPhoneCode)
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
                    value = text.value,
                    onValueChange = {
                        if (text.value != it && it.length  <= 10) {
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
                                            onCountryValueChange(it.countryPhoneCode)
                                        },
                                        defaultSelectedCountry = getLibCountries.single { it.countryCode == defaultLang },
                                        showCountryCode = showCountryCode,
                                    )
                                }
                            }
                    },
                    trailingIcon = {
                        IconButton(onClick = {
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