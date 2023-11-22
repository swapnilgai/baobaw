package com.java.baobaw.util

import dev.icerock.moko.media.Bitmap
enum class CompressFormat {
    JPEG, PNG
}

//public expect fun Bitmap.toCompressByteArray(
//    maxWidth: Int = 1200,
//    maxHeight: Int = 1200,
//    format: CompressFormat = CompressFormat.PNG,
//    quality: Int = 90
//): ByteArray


interface BitmapProcessor {
    fun toCompressByteArray(bitmap: Bitmap, maxWidth: Int = 1200, maxHeight: Int = 1200, format: CompressFormat = CompressFormat.PNG, quality: Int = 90): ByteArray
}

// AndroidMain
expect class BitmapProcessorImpl() : BitmapProcessor {
    override fun toCompressByteArray(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int,
        format: CompressFormat,
        quality: Int
    ): ByteArray
}