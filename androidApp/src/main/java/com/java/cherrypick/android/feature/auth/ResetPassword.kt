package com.java.cherrypick.android.feature.auth

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.java.cherrypick.android.BaseView
import com.java.cherrypick.android.R
import com.java.cherrypick.android.compose.ccp.component.CountryCodePicker
import com.java.cherrypick.android.compose.ccp.component.getFullPhoneNumber
import com.java.cherrypick.feature.auth.presentation.ResetPasswordViewModel
import com.java.cherrypick.feature.auth.presentation.UserExist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun ResetPasswordScreen(resetPasswordViewModel: ResetPasswordViewModel,
                        navController: NavController,
                        scope: CoroutineScope = rememberCoroutineScope()
) {

    var verifyUserState by remember { mutableStateOf<UserExist?>(null) }

    fun setContent(state: UserExist){
        verifyUserState = state
    }

    BaseView(viewModel = resetPasswordViewModel, navController = navController, setContentT = { state -> setContent(state)}) {
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val phoneNumber = remember { mutableStateOf("") }

            CountryCodePicker(
                text = phoneNumber.value,
                onValueChange = { phoneNumber.value = it },
                bottomStyle = false,
                shape = RoundedCornerShape(1.dp)
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Button(onClick = { scope.launch { resetPasswordViewModel.phoneExists(phoneNumber = getFullPhoneNumber()) }},
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cherry))
            ) {
                Text(text = stringResource(id = R.string.search))
            }

            Spacer(modifier = Modifier.padding(16.dp))
        }
    }
}
