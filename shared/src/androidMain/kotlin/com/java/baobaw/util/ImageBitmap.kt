package com.java.baobaw.util

// Android code (shared/src/androidMain/kotlin)
import dev.icerock.moko.media.Bitmap
import java.io.ByteArrayOutputStream

// AndroidMain
actual class BitmapProcessorImpl actual constructor() : BitmapProcessor {
    actual override fun toCompressByteArray(bitmap: Bitmap, maxWidth: Int, maxHeight: Int, format: CompressFormat, quality: Int): ByteArray {
        val ratio = bitmap.platformBitmap.width.toFloat() / bitmap.platformBitmap.height.toFloat()
        val (finalWidth, finalHeight) = if (ratio > 1) {
            Pair(maxWidth, (maxWidth / ratio).toInt())
        } else {
            Pair((maxHeight * ratio).toInt(), maxHeight)
        }

        val resizedBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap.platformBitmap, finalWidth, finalHeight, true)
        val byteArrayOutputStream = ByteArrayOutputStream()
        val androidFormat = when (format) {
            CompressFormat.JPEG -> android.graphics.Bitmap.CompressFormat.JPEG
            CompressFormat.PNG -> android.graphics.Bitmap.CompressFormat.PNG
        }
        resizedBitmap.compress(androidFormat, quality, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}
