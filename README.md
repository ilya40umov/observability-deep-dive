# Observability Deep Dive

## Modules

* [01-logback-mdc](01-logback-mdc) - Logback/MDC with executors, project reactor and coroutines
* [02-logback-brave](02-logback-brave) - Logback/MDC + OpenZipkin Brave with different flavors of concurrency
* [03-logback-otel](03-logback-otel) - Logback/MDC + OpenTelemetry, again with different flavors of concurrency 
* [04-spring-classic-simple](04-spring-classic-simple) - Spring MVC examples
* [05-spring-coroutines-simple](05-spring-coroutines-simple) - Spring WebFlux with coroutines

## Local Infra

* [compose.yml](compose.yml) that runs **Grafana**, **Loki**, **Tempo** and **Prometheus**
* [localhost:3000](http://localhost:3000/) is how you access **Grafana**
