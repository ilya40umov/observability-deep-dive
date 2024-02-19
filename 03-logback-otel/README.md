# 03-logback-otel

This module is showcasing how to use some APIs from OpenTelemetry.

* [gradle.build.kts](build.gradle.kts)
* [OpenTelemetry.kt](src/main/kotlin/me/ilya40umov/observability/helper/OpenTelemetry.kt)
* [OtelHelloWorldV1.kt](src/main/kotlin/me/ilya40umov/observability/OtelHelloWorldV1.kt)
* [OtelExecutorsV1.kt](src/main/kotlin/me/ilya40umov/observability/OtelExecutorsV1.kt)
* [OtelCoroutinesV1.kt](src/main/kotlin/me/ilya40umov/observability/OtelCoroutinesV1.kt)
* [OtelReactorV1.kt](src/main/kotlin/me/ilya40umov/observability/OtelReactorV1.kt)

## How does it work?

### OpenTelemetry

* uses `AutoConfiguredOpenTelemetrySdk` to create an instance of `OpenTelemetrySdk` that is used in other classes

### io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0

* this library comes with a logback appender implementation `io.opentelemetry.instrumentation.logback.mdc.v1_0.OpenTelemetryAppender`
* which can be registered in `logback.xml` in order to propagate baggage values into MDC context for logging 

### OtelHelloWorldV1

* creates a span by calling `io.opentelemetry.api.trace.Tracer.spanBuilder()`
* creates baggage via `io.opentelemetry.api.baggage.Baggage.builder()`
* creates a new context by adding the newly created span into the current context `Context.current().with(span)`
* add the baggage into the context and makes it current `baggage.storeInContext(context).makeCurrent()`
* later finishes the span via `span.end()`

### OtelExecutorsV1

* this code is analogous to the corresponding example using Brave
* `Context.taskWrapping()` is used in this case to enable "task wrapping" for an executor

### OtelCoroutinesV1

* this code is also similar to the corresponding example with Brave tracer
* however, OpenTelemetry comes with a bunch of nice extensions for Kotlin that are kept in `io.opentelemetry:opentelemetry-extension-kotlin`
* one of them is `io.opentelemetry.context.Context.asContextElement()`, which allows turning a given context into a coroutine context element
* adding resulting `CoroutineContext` element to coroutine will take care of restoring current span context on thread switches

### OtelReactorV1

* this example relies on `io.opentelemetry.instrumentation.reactor.v3_1.ContextPropagationOperator`
* `ContextPropagationOperator` comes from `io.opentelemetry.instrumentation:opentelemetry-reactor-3.1` library
* and first needs to be created and registered via `ContextPropagationOperator.create().registerOnEachOperator()`
* afterward, `ContextPropagationOperator.storeOpenTelemetryContext(reactorContext, context)` can be used to add span context into the reactor context