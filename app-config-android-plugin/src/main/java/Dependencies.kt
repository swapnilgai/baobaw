import Version.koinVersion

object Dependencies {
    const val androidXCore = "androidx.core:core-ktx:${Version.androidXCore}"
    const val appcompat = "androidx.appcompat:appcompat:${Version.appcompat}"
    const val junit = "junit:junit:${Version.junit}"
    const val junitTestExt = "androidx.test.ext:junit:${Version.junitTestExt}"
    const val espresso = "androidx.test.espresso:espresso-core:${Version.espresso}"
    const val material = "com.google.android.material:material:${Version.material}"
    const val koinAndroid = "io.insert-koin:koin-android:${Version.koinVersion}"
    const val koinTest = "io.insert-koin:koin-android-test:${Version.koinVersion}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Version.retrofit}"
    const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Version.retrofit}"
    const val okHttp = "com.squareup.okhttp3:okhttp:${Version.okHttp}"
    const val okHttpLogger = "com.squareup.okhttp3:logging-interceptor:${Version.okHttp}"
    const val androidCoreKtx = "androidx.core:core-ktx:${Version.androidktxCore}"
    const val androidKotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Version.kotlinVersion}"
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
    }
}