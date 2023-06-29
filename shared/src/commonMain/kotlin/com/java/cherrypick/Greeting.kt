package com.java.cherrypick

import io.github.jan.supabase.SupabaseClient

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}