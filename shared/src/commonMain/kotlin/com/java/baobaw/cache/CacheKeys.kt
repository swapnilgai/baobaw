package com.java.baobaw.cache


data class AuthSessionCacheKey(val key: String = "AuthSessionCacheKey"): CacheKey

data class UserExistCacheKey(val key: String): CacheKey

data class CurrentUserCacheKey(val key: String = "CurrentUserCacheKey"): CacheKey
