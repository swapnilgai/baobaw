package com.java.cherrypick.interactor

data class RetryOption(
    val retryCount: Int,
    val initialDelay: Long = 100,
    val maxDelay: Long = 1000,
    val delayIncrementalFactor: Double = 2.0,
)
