package com.java.cherrypick.model

import com.java.cherrypick.BuildKonfig

enum class ENVIRONMENT{
    DEVELOPMENT,
    PRODUCTION
}

abstract class ProjectEnvironment(
    val url: String,
    val environment: ENVIRONMENT,
    val apiKey: String,
    val onesignalAppId: String
)

class DevelopmentEnvironment() : ProjectEnvironment(
    url = BuildKonfig.apiUrl,
    environment = ENVIRONMENT.DEVELOPMENT,
    apiKey = BuildKonfig.apiKey,
    onesignalAppId = BuildKonfig.onesignalAppId
)

class ProductionEnvironment() : ProjectEnvironment(
    url = BuildKonfig.apiUrl,
    environment = ENVIRONMENT.PRODUCTION,
    apiKey = BuildKonfig.apiKey,
    onesignalAppId = BuildKonfig.onesignalAppId
)
