rootProject.name = "observability-deep-dive"

include("01-logback-mdc")
include("02-logback-brave")
include("03-logback-otel")
include("04-spring-classic-simple")
include("05-spring-coroutines-simple")

pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
}