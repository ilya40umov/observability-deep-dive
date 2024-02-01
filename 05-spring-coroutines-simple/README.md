# spring-coroutines-simple

This project is testing observability APIs in a coroutines / reactive setup.

* [LoggingControllerV1.kt](src/main/kotlin/me/ilya40umov/observability/LoggingControllerV1.kt)
* [LoggingCoRouterV1.kt](src/main/kotlin/me/ilya40umov/observability/LoggingCoRouterV1.kt)


## Some interesting classes
* `org.springframework.web.filter.reactive.ServerHttpObservationFilter`
* `org.springframework.boot.actuate.autoconfigure.observation.web.reactive.WebFluxObservationAutoConfiguration`

## Links

* [Observability with Spring Boot 3](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3)
* [Distributed Tracing Reference Guide](https://micrometer.io/docs/tracing)
