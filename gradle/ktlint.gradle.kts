val ktlint by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:0.50.0")
}

val inputFile = fileTree(mapOf("dir" to rootDir, "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
    inputs.files(inputFile)

    group = "formatting"
    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("src/**/*.kt", "**/*.gradle.kts")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    inputs.files(inputFile)

    group = "formatting"
    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("-F", "src/**/*.kt", "**/*.gradle.kts")
}

val testAllUnitTests by tasks.register("testAllUnitTests") {
    group = "verification"
    description = "Run all tests"
    dependsOn(
        "testDebugUnitTest",
    )
}
