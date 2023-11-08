package com.java.baobaw.util

import dev.icerock.moko.media.Bitmap
enum class CompressFormat {
    JPEG, PNG
}

expect fun Bitmap.toCompressByteArray(
    maxWidth: Int = 800,
    maxHeight: Int = 600,
    format: CompressFormat = CompressFormat.PNG,
    quality: Int = 70
): ByteArray