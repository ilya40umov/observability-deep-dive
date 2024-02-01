plugins {
    kotlin("jvm")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")

    implementation("io.projectreactor:reactor-core")
    implementation("io.micrometer:context-propagation")
}

