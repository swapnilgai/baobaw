package com.java.baobaw

import io.github.jan.supabase.SupabaseClient

class Greeting {
    private val platform: com.java.baobaw.Platform = com.java.baobaw.getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}