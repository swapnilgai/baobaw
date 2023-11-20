object Dependencies {
    const val androidXCore = "androidx.core:core-ktx:${Version.androidXCore}"
    const val appcompat = "androidx.appcompat:appcompat:${Version.appcompat}"
    const val junit = "junit:junit:${Version.junit}"
    const val junitTestExt = "androidx.test.ext:junit:${Version.junitTestExt}"
    const val espresso = "androidx.test.espresso:espresso-core:${Version.espresso}"
    const val material = "com.google.android.material:material:${Version.material}"
    const val koinAndroid = "io.insert-koin:koin-android:${Version.koinVersion}"
    const val koinTest = "io.insert-koin:koin-android-test:${Version.koinVersion}"
    const val androidCoreKtx = "androidx.core:core-ktx:${Version.androidktxCore}"
    const val androidKotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Version.kotlinVersion}"
    const val koin = "io.insert-koin:koin-core:${Version.koin}"
    const val mockk = "io.mockk:mockk:${Version.mockk}"
    object Compose {
        const val compiler = "androidx.compose.compiler:compiler:${Version.compose}"
        const val runtime = "androidx.compose.runtime:runtime:${Version.compose}"
        const val ui = "androidx.compose.ui:ui:${Version.compose}"
        const val foundation = "androidx.compose.foundation:foundation:${Version.compose}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:${Version.composeConstraintLayout}"
        const val tooling = "androidx.compose.ui:ui-tooling:${Version.compose}"
        const val toolingUi = "androidx.compose.ui:ui-tooling-preview:${Version.compose}"
        const val material = "androidx.compose.material:material:${Version.compose}"
        const val activity = "androidx.activity:activity-compose:${Version.composeActivity}"
        const val materialIconExtended = "androidx.compose.material:material-icons-extended:${Version.compose}"
    }

    object Shared {

        val commonMain = listOf(
            "org.jetbrains.kotlinx:kotlinx-serialization-core:${Version.serialization}",
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}",
            "io.ktor:ktor-client-core:${Version.ktor}",
            "io.ktor:ktor-client-content-negotiation:${Version.ktor}",
            "io.ktor:ktor-serialization-kotlinx-json:${Version.ktor}",
            "io.ktor:ktor-client-logging:${Version.ktor}",
             koin,
            "co.touchlab:kermit:${Version.kermit}",
            "org.jetbrains.kotlinx:kotlinx-datetime:${Version.kotlinxDatetime}"
        )

        val commonTest = listOf(
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Version.coroutines}",
            "io.ktor:ktor-client-mock:${Version.ktor}",
            "io.insert-koin:koin-test:${Version.koin}",
            "io.mockk:mockk-common:${Version.mockkCommon}",
            "app.cash.turbine:turbine:${Version.turbine}",
            junit
        )

        val commonKotlin = listOf(
            "stdlib-common"
        )

        val commonKotlinTest = listOf(
            "test-common",
            "test-annotations-common"
        )

        val androidMain = listOf(
            "io.ktor:ktor-client-okhttp:${Version.ktor}"
        )

        val androidKotlinTest = listOf(
            "test-junit"
        )

        val androidTest = listOf(
            "junit:junit:4.13.2",
            mockk,
            "app.cash.turbine:turbine:${Version.turbine}"
        )

        val iosMain = listOf(
            "io.ktor:ktor-client-darwin:${Version.ktor}"
        )

        val supabase = listOf(
            "io.github.jan-tennert.supabase:gotrue-kt:${Version.supabase}",
            "io.github.jan-tennert.supabase:postgrest-kt:${Version.supabase}",
            "io.github.jan-tennert.supabase:realtime-kt:${Version.supabase}",
            "io.github.jan-tennert.supabase:storage-kt:${Version.supabase}"
        )

        val supabasePlatform = "io.github.jan-tennert.supabase:bom:${Version.supabase}"

        val android = listOf(
            "androidx.compose.ui:ui:${Version.compose}",
            "androidx.compose.ui:ui-tooling:${Version.compose}",
            "androidx.compose.ui:ui-tooling-preview:${Version.compose}",
            "androidx.compose.foundation:foundation:${Version.compose}",
            "androidx.compose.material:material:${Version.compose}",
            "androidx.compose.material:material-icons-extended:${Version.compose}",
            "androidx.compose.material3:material3:${Version.material3}",
            "androidx.activity:activity-compose:${Version.activity}",
            "androidx.navigation:navigation-compose:${Version.navigation}",
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutines}",
            "io.insert-koin:koin-android:${Version.koinVersion}",
            "io.insert-koin:koin-androidx-compose:${Version.koinCompose}",
            "io.coil-kt:coil-compose:${Version.coil}",
            "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.lifecycle}",
            "androidx.lifecycle:lifecycle-runtime-ktx:${Version.lifecycle}",
            "androidx.lifecycle:lifecycle-runtime-compose:${Version.lifecycle}",
            "androidx.lifecycle:lifecycle-viewmodel-compose:${Version.lifecycle}",
            "androidx.lifecycle:lifecycle-livedata-ktx:${Version.lifecycle}"
        )

        val iceRock = listOf(
            "dev.icerock.moko:resources:${Version.mokoResourcesGenerator}",
            "dev.icerock.moko:permissions-compose:${Version.mokoPermissions}",
            "dev.icerock.moko:mvvm-compose:${Version.mokoMvvmVersion}",
            "dev.icerock.moko:geo:${Version.mokoGeo}",
            "dev.icerock.moko:media-compose:${Version.mokoMedia}"
        )
    }
}