# 05-spring-coroutines-simple

This project is testing observability APIs in a coroutines / reactive setup.

* [build.gradle.kts](build.gradle.kts)
* [spring-coroutines-simple.http](spring-coroutines-simple.http)
* [ContextPropagationConfig.kt](src/main/kotlin/me/ilya40umov/observability/config/ContextPropagationConfig.kt)
* [HelloControllerV1.kt](src/main/kotlin/me/ilya40umov/observability/HelloControllerV1.kt)
* [AlohaCoRouterV1.kt](src/main/kotlin/me/ilya40umov/observability/AlohaCoRouterV1.kt)
* [UserAddingFilter.kt](src/main/kotlin/me/ilya40umov/observability/UserAddingFilter.kt)

## How does it work?

### io.micrometer:context-propagation

* defines `io.micrometer.context.ThreadLocalAccessor` and other abstractions for working with thread-local values
* `io.projectreactor:reactor-core` uses these abstractions to implement optional hooks that will be triggered for each time computation is switching to a different thread

### io.micrometer:micrometer-observation

* implements `io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor` to put and restore current Observation

### io.micrometer:micrometer-tracing

* implements `io.micrometer.tracing.contextpropagation.ObservationAwareSpanThreadLocalAccessor` to put and restore current Span
* defines `io.micrometer.tracing.handler.TracingObservationHandler` that reacts to Observation lifecycle

## Links

* [Observability with Spring Boot 3](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3)
* [Distributed Tracing Reference Guide](https://micrometer.io/docs/tracing)
