package com.java.baobaw

interface Platform {
    val name: String
}

expect fun getPlatform(): com.java.baobaw.Platform

