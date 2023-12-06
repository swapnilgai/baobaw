package com.java.baobaw.cache

const val INITIAL_PAGE_NUMBER = 0
const val DEFAULT_PAGE_SIZE = 20

data class AuthSessionCacheKey(val key: String = "AuthSessionCacheKey"): CacheKey

data class UserExistCacheKey(val key: String): CacheKey

data class CurrentUserCacheKey(val key: String = "CurrentUserCacheKey"): CacheKey

data class LastMessagesTotalCount(val key: String = "LastMessagesTotalCount"): CacheKey

data class UserMessagesKey(val key: String = "UserMessagesKey"): CacheKey

data class MessageDetailKey(val referenceId: String): CacheKey

data class PageMessageDetailKey(val referenceId: String): CacheKey


data class MessageDetailCount(val referenceId: String): CacheKey


