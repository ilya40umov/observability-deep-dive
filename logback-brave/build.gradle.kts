plugins {
    kotlin("jvm")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.11")

    implementation(platform("io.zipkin.brave:brave-bom:5.15.1"))
    implementation("io.zipkin.brave:brave")
    implementation("io.zipkin.brave:brave-context-slf4j")

    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
}

