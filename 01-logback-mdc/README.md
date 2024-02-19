# 01-logback-mdc

This module is showcasing how to use the MDC API from Slf4j / Logback.

* [build.gradle.kts](build.gradle.kts)
* [LogbackHelloWorldV1.kt](src/main/kotlin/me/ilya40umov/observability/LogbackHelloWorldV1.kt)
* [LogbackMdcClassicV1.kt](src/main/kotlin/me/ilya40umov/observability/LogbackMdcClassicV1.kt)
* [LogbackMdcCoroutinesV1.kt](src/main/kotlin/me/ilya40umov/observability/LogbackMdcCoroutinesV1.kt)
* [LogbackMdcReactorV1.kt](src/main/kotlin/me/ilya40umov/observability/LogbackMdcReactorV1.kt)

## How does it work?

### LogbackHelloWorldV1

* `org.slf4j.MDC` is used to manipulate values in MDC directly
* `MDC.put()` is used to set a key/value pair and `MDC.clear()` clears all values 
* `MDC` is internally working with an instance of `ch.qos.logback.classic.util.LogbackMDCAdapter`
* `LogbackMDCAdapter` is based on `ThreadLocal` variables

### LogbackMdcClassicV1

* `org.slf4j.MDC` is used here again
* `MDC.putCloseable` method does not restore previous value that was stored under the key in MDC
* `MDC.getCopyOfContextMap` and `MDC.setContextMap` allow to capture and restore MDC state

### LogbackMdcCoroutinesV1

* when working with coroutines / suspending methods, we can't use `org.slf4j.MDC` directly as coroutine may switch threads easily
* `kotlinx.coroutines.slf4j.MDCContext` can be provided with a map of values and added to coroutine scope via `withContext`
* `MDCContext` extends `kotlinx.coroutines.ThreadContextElement` and takes care of updating MDC on each thread switch

### LogbackMdcReactorV1

* when using `Flux` / `Mono` we can't rely on `org.slf4j.MDC` again
* here we can take advantage of `io.micrometer:context-propagation` library and `reactor.core.publisher.Hooks`
* `Hooks.enableAutomaticContextPropagation()` allows the library to instrument project reactor machinery
* afterward, we can interact with `io.micrometer.context.ContextRegistry` via `registerThreadLocalAccessor`
* this allows us to make context propagation library aware of thread local state we would like to maintain
* at this point we can use `contextWrite` on `Flux` to set the actual values we would like to propagate