import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.4.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    val kotlinVersion = "2.1.20"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
}

allprojects {
    group = "me.ilya40umov"
    version = "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }
}

subprojects {
    apply<JavaPlugin>()
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    configurations {
        named("compileOnly") {
            extendsFrom(named("annotationProcessor").get())
        }
    }
    dependencies {
        val otelVersion = "1.48.0"
        val otelAlphaVersion = "1.31.0-alpha"
        val kotestVersion = "5.9.1"
        // TODO update brave & zipkin reporter as soon as Spring Boot updates their versions
        "implementation"(platform("io.zipkin.brave:brave-bom:6.2.0"))
        "implementation"(platform("io.zipkin.reporter2:zipkin-reporter-bom:3.5.0"))
        "implementation"(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.1"))
        "implementation"(platform("io.opentelemetry:opentelemetry-bom:$otelVersion"))
        "implementation"(platform("io.opentelemetry:opentelemetry-bom-alpha:$otelAlphaVersion"))
        "implementation"(platform("org.junit:junit-bom:5.12.2"))
        "testImplementation"("io.kotest:kotest-runner-junit5:$kotestVersion")
        "testImplementation"("io.kotest:kotest-assertions-core:$kotestVersion")
        "testImplementation"("org.junit.jupiter:junit-jupiter-api")
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine")
        constraints {
            "implementation"("ch.qos.logback:logback-classic:1.5.18")
            "implementation"("com.github.loki4j:loki-logback-appender:1.6.0")
            "implementation"("io.projectreactor:reactor-core:3.7.2")
            "implementation"("io.micrometer:context-propagation:1.1.2")
            "implementation"("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:$otelAlphaVersion")
            "implementation"("io.opentelemetry.instrumentation:opentelemetry-reactor-3.1:$otelAlphaVersion")
        }
    }
    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs += "-Xjsr305=strict"
                jvmTarget = "17"
            }
        }
        withType<Test> {
            useJUnitPlatform {
                testLogging.showStandardStreams = true
            }
        }
    }
}
