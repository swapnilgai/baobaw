package com.java.baobaw

class AndroidPlatform : com.java.baobaw.Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): com.java.baobaw.Platform = AndroidPlatform()
