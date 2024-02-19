# Observability Deep Dive

## Modules

* [01-logback-mdc](01-logback-mdc) - Logback/MDC with executors, project reactor and coroutines
* [02-logback-brave](02-logback-brave) - Logback/MDC + OpenZipkin Brave with different flavors of concurrency
* [03-logback-otel](03-logback-otel) - Logback/MDC + OpenTelemetry, again with different flavors of concurrency 
* [04-spring-classic-simple](04-spring-classic-simple) - Spring MVC examples
* [05-spring-coroutines-simple](05-spring-coroutines-simple) - Spring WebFlux with coroutines
* [06-service-a](06-service-a) - Service A is a simple service based on WebFlux/coroutines which calls Service B 
* [06-service-b](06-service-b) - Service B is another service based on WebFlux/coroutines which gets called by Service A

## Local Infra

* [compose.yml](compose.yml) that runs **Grafana**, **Loki**, **Tempo** and **Prometheus**
* [localhost:3000](http://localhost:3000/) is how you access **Grafana**

## Open Issues

* Kotlin coroutine context propagation [openzipkin/brave/issues/820](https://github.com/openzipkin/brave/issues/820)
* Traceid and spanid wrong when using Kotlin Reactor Coroutines [micrometer-metrics/tracing/issues/174](https://github.com/micrometer-metrics/tracing/issues/174)
* Observation API does not support Baggage [micrometer-metrics/tracing/issues/455](https://github.com/micrometer-metrics/tracing/issues/455) and [micrometer-metrics/tracing/issues/563](https://github.com/micrometer-metrics/tracing/issues/563)
* Better support for working with Observation from Kotlin [micrometer-metrics/micrometer/issues/4754](https://github.com/micrometer-metrics/micrometer/issues/4754)

## Links

### Logback

* https://logback.qos.ch/manual/

### Loki4j Logback

* https://loki4j.github.io/loki-logback-appender/docs/configuration

### Coroutines & MDC

* https://github.com/Kotlin/kotlinx.coroutines/tree/master/integration/kotlinx-coroutines-slf4j
* https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/as-context-element.html

### Context Propagation With Project Reactor

* https://spring.io/blog/2023/03/28/context-propagation-with-project-reactor-1-the-basics/
* https://spring.io/blog/2023/03/29/context-propagation-with-project-reactor-2-the-bumpy-road-of-spring-cloud/
* https://spring.io/blog/2023/03/30/context-propagation-with-project-reactor-3-unified-bridging-between-reactive/

### OpenZipkin Brave

* https://github.com/openzipkin/brave/tree/master/brave

### Open Telemetry

* https://opentelemetry.io/docs/languages/java/instrumentation/
* https://github.com/open-telemetry/opentelemetry-java/blob/main/sdk-extensions/autoconfigure/README.md
* [io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/logback/logback-mdc-1.0/library)
* [io.opentelemetry:opentelemetry-extension-kotlin](https://github.com/open-telemetry/opentelemetry-java/tree/main/extensions/kotlin)
* [io.opentelemetry.instrumentation:opentelemetry-reactor-3.1](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/reactor/reactor-3.1)

### Micrometer

* [Micrometer Observation](https://docs.micrometer.io/micrometer/reference/observation.html)
* [Context Propagation support](https://docs.micrometer.io/micrometer/reference/contextpropagation.html)
* [Distributed Tracing Reference Guide](https://micrometer.io/docs/tracing)
* https://micrometer.io/docs/contextPropagation

### Spring Boot

* [Observability with Spring Boot 3](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3)
