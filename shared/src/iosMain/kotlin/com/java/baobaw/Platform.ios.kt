package com.java.baobaw

import com.java.baobaw.Platform
import platform.UIKit.UIDevice

class IOSPlatform: com.java.baobaw.Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): com.java.baobaw.Platform = IOSPlatform()

fun getCache() = null
