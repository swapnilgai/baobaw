package com.java.cherrypick

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

