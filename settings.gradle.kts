rootProject.name = "observability-deep-dive"

include("logback-brave")
include("spring-classic-simple")
include("spring-coroutines-simple")
include("spring-coroutines-advanced")
include("spring-co-web-filter")

pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
}