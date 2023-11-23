rootProject.name = "observability-deep-dive"

include("spring-classic-simple")
include("spring-coroutines-simple")
include("spring-coroutines-advanced")

pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
}