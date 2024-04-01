import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.2" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    kotlin("jvm") version "1.9.22" apply false
    kotlin("plugin.spring") version "1.9.22" apply false
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
        val otelVersion = "1.36.0"
        val otelAlphaVersion = "1.31.0-alpha"
        "implementation"(platform("io.zipkin.brave:brave-bom:5.16.0"))
        "implementation"(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.7.3"))
        "implementation"(platform("io.opentelemetry:opentelemetry-bom:$otelVersion"))
        "implementation"(platform("io.opentelemetry:opentelemetry-bom-alpha:$otelAlphaVersion"))
        constraints {
            "implementation"("ch.qos.logback:logback-classic:1.4.12")
            "implementation"("com.github.loki4j:loki-logback-appender:1.4.2")
            "implementation"("io.projectreactor:reactor-core:3.6.2")
            "implementation"("io.micrometer:context-propagation:1.1.0")
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
            useJUnitPlatform()
        }
    }
}
