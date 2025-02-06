package com.java.baobaw.cache


interface CacheExpirationPolicy {
    fun isEntryExpired(requestTimestamp: Long): Boolean
}

data class ExpireAtInstant(val instantMillis: Long) : CacheExpirationPolicy {
    override fun isEntryExpired(requestTimestamp: Long): Boolean =
        Clock.currentMillis() >= instantMillis
}

data class ExpireAfterTimeout(val timeoutDurationMillis: Long) : CacheExpirationPolicy {
    override fun isEntryExpired(requestTimestamp: Long): Boolean =
        Clock.currentMillis() >= requestTimestamp + timeoutDurationMillis
}

data class MergeExpirationPolicies(val policies: Iterable<CacheExpirationPolicy>) : CacheExpirationPolicy {
    override fun isEntryExpired(requestTimestamp: Long): Boolean =
        policies.any { it.isEntryExpired(requestTimestamp) }
}


