package com.java.baobaw.util

// iOS code (shared/src/iosMain/kotlin)
import dev.icerock.moko.media.Bitmap
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIImage
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImageJPEGRepresentation
import kotlinx.cinterop.useContents
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret

fun Bitmap.toCompressByteArray(maxWidth: Int, maxHeight: Int, format: CompressFormat, quality: Int): ByteArray {
//    val uiImage = this.image
//    val currentSize = uiImage.size.useContents {
//        this
//    }
//    val ratio = currentSize.width / currentSize.height
//    val (finalWidth, finalHeight) = if (ratio > 1) {
//        Pair(maxWidth.toDouble(), (maxWidth / ratio))
//    } else {
//        Pair((maxHeight * ratio), maxHeight.toDouble())
//    }
//
//    UIGraphicsBeginImageContextWithOptions(CGSizeMake(finalWidth, finalHeight), false, 0.0)
//    uiImage.drawInRect(CGRectMake(0.0, 0.0, finalWidth, finalHeight))
//
//    val newImage = UIGraphicsGetImageFromCurrentImageContext()
//    UIGraphicsEndImageContext()
//
//    val imageData = when (format) {
//        CompressFormat.JPEG -> newImage?.let { UIImageJPEGRepresentation(it, quality / 100.0) }
//        CompressFormat.PNG -> newImage?.let { UIImageJPEGRepresentation(it, quality / 100.0) } // PNG compression level is usually ignored
//    }
//
//    val bytes = imageData?.bytes ?: throw IllegalArgumentException("image bytes is null")
//    val length = imageData.length
//
//    val data: CPointer<ByteVar> = bytes.reinterpret()
//    return ByteArray(length.toInt()) { index -> data[index] }
    return byteArrayOf()
}


actual class BitmapProcessorImpl actual constructor() : BitmapProcessor {
    actual override fun toCompressByteArray(bitmap: Bitmap, maxWidth: Int, maxHeight: Int, format: CompressFormat, quality: Int): ByteArray {
        return byteArrayOf()
    }
}
