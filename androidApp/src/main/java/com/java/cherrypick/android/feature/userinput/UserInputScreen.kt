package com.java.cherrypick.android.feature.userinput

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.java.cherrypick.SharedRes
import com.java.cherrypick.android.util.stringResource

@Composable
fun UserInputScreen() {
    Text(
        text = stringResource(SharedRes.strings.ok),
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
    )
}