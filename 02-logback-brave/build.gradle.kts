plugins {
    kotlin("jvm")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")

    implementation("io.zipkin.brave:brave")
    implementation("io.zipkin.brave:brave-context-slf4j")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation("io.projectreactor:reactor-core")
    implementation("io.micrometer:context-propagation")
}

