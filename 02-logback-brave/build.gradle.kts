plugins {
    kotlin("jvm")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("com.github.loki4j:loki-logback-appender")

    implementation("io.zipkin.brave:brave")
    implementation("io.zipkin.brave:brave-context-slf4j")
    implementation("io.zipkin.reporter2:zipkin-sender-urlconnection")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")

    implementation("io.projectreactor:reactor-core")
    implementation("io.micrometer:context-propagation")
}

