# 04-spring-classic-simple

This project is showcasing Micrometer APIs using classic thread-per-request approach.

* [build.gradle.kts](build.gradle.kts)
* [spring-classic-simple.http](spring-classic-simple.http)
* [HelloControllerV1.kt](src/main/kotlin/me/ilya40umov/observability/HelloControllerV1.kt)
* [HelloControllerV2.kt](src/main/kotlin/me/ilya40umov/observability/HelloControllerV2.kt)
* [HelloControllerV3.kt](src/main/kotlin/me/ilya40umov/observability/HelloControllerV3.kt)

## How does it work?

### io.micrometer:micrometer-tracing

* defines `io.micrometer.tracing.handler.TracingObservationHandler` that reacts to Observation lifecycle
* implements multiple versions of `TracingObservationHandler`, e.g. `PropagatingReceiverTracingObservationHandler`

### io.micrometer:micrometer-tracing-bridge-brave

* a library implementing a set of abstractions from `io.micrometer:micrometer-tracing` via delegating to Brave tracer
* when this library is on the classpath, `org.springframework.boot:spring-boot-actuator-autoconfigure` will set up tracing via Brave
* autoconfiguration is done by `org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration`
* while also more configuration is loaded from `org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations`
* `BraveAutoConfiguration` sets up `ThreadLocalCurrentTraceContext` to allow for context propagation based on thread-locals
* and `BravePropagationConfigurations` will take care of adding `MDCScopeDecorator` to Brave Tracer

### io.micrometer:micrometer-tracing-bridge-otel

* bridges to Otel tracer instead of Brave
* contains more code overall compared to Brave bridge (due to bigger diff between APIs)
* adding Baggage values to MDC is handled by [Slf4JBaggageEventListener](https://github.com/micrometer-metrics/tracing/blob/main/micrometer-tracing-bridges/micrometer-tracing-bridge-otel/src/main/java/io/micrometer/tracing/otel/bridge/Slf4JBaggageEventListener.java)

### HelloControllerV1

* Observation is created by `org.springframework.web.filter.ServerHttpObservationFilter`
* `PropagatingReceiverTracingObservationHandler` reacts to Observation getting created and creates a span via Tracer
* Observation is then put into scope by `ServerHttpObservationFilter`
* `PropagatingReceiverTracingObservationHandler` reacts to it and makes the associated span "current"
