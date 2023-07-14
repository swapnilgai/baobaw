package com.java.cherrypick.android.feature.userinput

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun UserInputScreen() {
    Text(
        text = "User Input Screen",
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
    )
}