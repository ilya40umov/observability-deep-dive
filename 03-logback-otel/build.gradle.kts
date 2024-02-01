plugins {
    kotlin("jvm")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")

    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-context")
    implementation("io.opentelemetry:opentelemetry-extension-kotlin")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
}

