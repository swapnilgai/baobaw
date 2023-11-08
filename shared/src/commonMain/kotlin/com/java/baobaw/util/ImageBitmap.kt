package com.java.baobaw.util

import dev.icerock.moko.media.Bitmap
enum class CompressFormat {
    JPEG, PNG
}

expect fun Bitmap.toCompressByteArray(
    maxWidth: Int = 1200,
    maxHeight: Int = 1200,
    format: CompressFormat = CompressFormat.PNG,
    quality: Int = 90
): ByteArray