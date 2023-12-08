import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.3" apply false
    kotlin("jvm") version "1.8.22" apply false
    kotlin("plugin.spring") version "1.8.22" apply false
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
