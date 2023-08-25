package com.java.cherrypick.cache


data class AuthSessionCacheKey(val key: String = "AuthSessionCacheKey"): CacheKey

data class UserExistCacheKey(val key: String): CacheKey