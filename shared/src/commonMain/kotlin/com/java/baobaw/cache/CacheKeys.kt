package com.java.baobaw.cache


data class AuthSessionCacheKey(val key: String = "AuthSessionCacheKey"): CacheKey

data class UserExistCacheKey(val key: String): CacheKey

data class CurrentUserCacheKey(val key: String = "CurrentUserCacheKey"): CacheKey

data class LastMessagesTotalCount(val key: String = "LastMessagesTotalCount"): CacheKey

data class UserMessagesKey(val key: String = "UserMessagesKey",  val pageNumber: Long = 0, val pageSize: Long = 20): CacheKey

