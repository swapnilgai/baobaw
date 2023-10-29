package com.java.baobaw.android.feature.userinput

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.java.baobaw.SharedRes
import com.java.baobaw.android.util.stringResource

@Composable
fun UserInputScreen() {
    Text(
        text = stringResource(com.java.baobaw.SharedRes.strings.ok),
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
    )
}