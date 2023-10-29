package com.java.baobaw.android.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.java.baobaw.util.Strings
import dev.icerock.moko.resources.StringResource

@Composable
fun stringResource(id: StringResource, vararg args: Any): String {
    return com.java.baobaw.util.Strings(LocalContext.current).get(id, args.toList())
}
