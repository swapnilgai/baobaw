package com.java.baobaw.interactor

data class RetryOption<T>(
    val retryCount: Int,
    val initialDelay: Long = 100,
    val maxDelay: Long = 1000,
    val delayIncrementalFactor: Double = 2.0,
    val retryCondition: (Result<T>) -> Boolean = {it.isFailure},
    val forceRefreshDuringRetry: Boolean = true,
    val throwException: Boolean = true
)
