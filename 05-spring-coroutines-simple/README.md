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

* defines [io.micrometer.context.ThreadLocalAccessor](https://github.com/micrometer-metrics/context-propagation/blob/main/context-propagation/src/main/java/io/micrometer/context/ThreadLocalAccessor.java) interface
* `io.projectreactor:reactor-core` uses `ThreadLocalAccessor` and other abstractions defined in Micrometer to implement optional hooks that will be triggered for each time computation is switching threads

### io.micrometer:micrometer-observation

* implements [io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-observation/src/main/java/io/micrometer/observation/contextpropagation/ObservationThreadLocalAccessor.java) to put and restore current Observation

### io.micrometer:micrometer-tracing

* implements [io.micrometer.tracing.contextpropagation.ObservationAwareSpanThreadLocalAccessor](https://github.com/micrometer-metrics/tracing/blob/main/micrometer-tracing/src/main/java/io/micrometer/tracing/contextpropagation/ObservationAwareSpanThreadLocalAccessor.java) to put and restore current Span
* defines [io.micrometer.tracing.handler.TracingObservationHandler](https://github.com/micrometer-metrics/tracing/blob/main/micrometer-tracing/src/main/java/io/micrometer/tracing/handler/TracingObservationHandler.java) which reacts to lifecycle events for Observation

### HelloControllerV1

* Observation is created, started, and added to context under `ObservationThreadLocalAccessor.KEY` by [org.springframework.web.server.adapter.HttpWebHandlerAdapter](https://github.com/spring-projects/spring-framework/blob/main/spring-web/src/main/java/org/springframework/web/server/adapter/HttpWebHandlerAdapter.java)
* Span is then created, started, and made current by [io.micrometer.tracing.handler.PropagatingReceiverTracingObservationHandler](https://github.com/micrometer-metrics/tracing/blob/main/micrometer-tracing/src/main/java/io/micrometer/tracing/handler/PropagatingReceiverTracingObservationHandler.java)
* For the nested Observation, Span is handled by [io.micrometer.tracing.handler.DefaultTracingObservationHandler](https://github.com/micrometer-metrics/tracing/blob/main/micrometer-tracing/src/main/java/io/micrometer/tracing/handler/DefaultTracingObservationHandler.java) 