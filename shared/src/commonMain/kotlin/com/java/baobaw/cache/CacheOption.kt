package com.java.baobaw.cache

data class CacheOptions(
    val key: CacheKey,
    val secondaryKey: CacheKey? = null,
    val expirationPolicy: CacheExpirationPolicy = defaultCacheExpiration,
    val allowOverwrite: Boolean = true
)

fun CacheOptions(
    key: String,
    secondaryKey: String? = null,
    expirationPolicy: CacheExpirationPolicy = defaultCacheExpiration
): CacheOptions {
    return CacheOptions(
        key = validateCacheKey(key),
        secondaryKey = secondaryKey?.let { validateCacheKey(it) },
        expirationPolicy = expirationPolicy
    )
}

fun CacheOptions(
    key: String,
    secondaryKey: String? = null,
    expirationPolicy: Iterable<CacheExpirationPolicy>
): CacheOptions {
    return CacheOptions(
        key = validateCacheKey(key),
        secondaryKey = secondaryKey?.let { validateCacheKey(it) },
        expirationPolicy = MergeExpirationPolicies(expirationPolicy)
    )
}

fun CacheOptions(
    key: CacheKey,
    secondaryKey: CacheKey? = null,
    expirationPolicy: Iterable<CacheExpirationPolicy>
): CacheOptions {
    return CacheOptions(
        key = key,
        secondaryKey = secondaryKey,
        expirationPolicy = MergeExpirationPolicies(expirationPolicy)
    )
}

private fun validateCacheKey(key: String): CacheKey {
    return if (key.isNotEmpty() && key.isNotBlank()) {
        StringCacheKey(key)
    } else {
        throw IllegalArgumentException("Cache key must not be empty or blank")
    }
}

private const val DEFAULT_CACHE_EXPIRATION_DURATION = 1000L * 60 * 15 // 15 minutes
private val defaultCacheExpiration = ExpireAfterTimeout(DEFAULT_CACHE_EXPIRATION_DURATION)
