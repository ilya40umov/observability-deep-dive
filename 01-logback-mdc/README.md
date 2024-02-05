# 01-logback-mdc

This module is showcasing how to use the MDC API from Slf4j / Logback.

* [LogbackMdcClassicV1.kt](src/main/kotlin/me/ilya40umov/observability/LogbackMdcClassicV1.kt)
* [LogbackMdcCoroutinesV1.kt](src/main/kotlin/me/ilya40umov/observability/LogbackMdcCoroutinesV1.kt)
* [LogbackMdcReactorV1.kt](src/main/kotlin/me/ilya40umov/observability/LogbackMdcReactorV1.kt)

## Links 

### Logback

* https://logback.qos.ch/manual/

### Loki4j Logback

* https://loki4j.github.io/loki-logback-appender/docs/configuration

### Coroutines & MDC

* https://github.com/Kotlin/kotlinx.coroutines/tree/master/integration/kotlinx-coroutines-slf4j
* https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/as-context-element.html

### Context Propagation With Project Reactor

* https://micrometer.io/docs/contextPropagation
* https://spring.io/blog/2023/03/28/context-propagation-with-project-reactor-1-the-basics/
* https://spring.io/blog/2023/03/29/context-propagation-with-project-reactor-2-the-bumpy-road-of-spring-cloud/
* https://spring.io/blog/2023/03/30/context-propagation-with-project-reactor-3-unified-bridging-between-reactive/