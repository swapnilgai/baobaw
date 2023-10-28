package com.java.baobaw.cache


data class AuthSessionCacheKey(val key: String = "AuthSessionCacheKey"):
    com.java.baobaw.cache.CacheKey

data class UserExistCacheKey(val key: String): com.java.baobaw.cache.CacheKey