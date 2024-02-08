# 02-logback-brave

This module is showcasing how to use some APIs from Brave.

* [build.gradle.kts](build.gradle.kts)
* [TracingFactory.kt](src/main/kotlin/me/ilya40umov/observability/helper/TracingFactory.kt)
* [BraveHelloWorldV1.kt](src/main/kotlin/me/ilya40umov/observability/BraveHelloWorldV1.kt)
* [BraveExecutorsV1.kt](src/main/kotlin/me/ilya40umov/observability/BraveExecutorsV1.kt)
* [BraveExecutorsV2.kt](src/main/kotlin/me/ilya40umov/observability/BraveExecutorsV2.kt)
* [BraveCoroutinesV1.kt](src/main/kotlin/me/ilya40umov/observability/BraveCoroutinesV1.kt)
* [BraveCoroutinesV2.kt](src/main/kotlin/me/ilya40umov/observability/BraveCoroutinesV2.kt)
* [BraveReactorV1.kt](src/main/kotlin/me/ilya40umov/observability/BraveReactorV1.kt)

## How does it work?

### TracingFactory

* this factory is used in all examples in this project to create an instance of `brave.Tracing`
* it first creates an instance of `zipkin2.reporter.brave.AsyncZipkinSpanHandler` for reporting spans to Tempo
* then, it proceeds with `Tracing.newBuilder()` which is provided with sampling, naming and other options
* `brave.propagation.ThreadLocalCurrentTraceContext` is created for propagating the trace context
* this is where `brave.context.slf4j.MDCScopeDecorator` gets registered to take care of putting baggage into MDC
* additionally, `brave.baggage.BaggagePropagation` is configured to propagate each provided baggage field locally

### BraveHelloWorldV1

* `tracer.newTrace()` will create a new `brave.Span` (trace) with a given name
* `Span.tag()` is used to set tags on a trace (don't confuse this with baggage though)
* `Span.start()` is called to signal that span has started
* `brave.baggage.BaggageField.updateValue()` is used to set a baggage value
* `tracer.withSpanInScope(trace)` helps with setting the trace as current for a block of code
* `Span.finish()` is needed to signal that the span has ended and can be reported to Tempo

### BraveExecutorsV1

* here we create a couple of thread pools / executors for doing processing of each user in 2 stages
* we use APIs from previous example, but have to run `tracer.withSpanInScope(trace)` within each task

### BraveExecutorsV2

* this is an improved version of the previous example with executors
* here each executor is wrapped with `tracing.currentTraceContext().executorService()`
* this allows to avoid calling `tracer.withSpanInScope()` on the nested tasks, as those inherit the trace context
* underneath, `CurrentTraceContextExecutorService` is used to capture context of the current trace at the time of calling `.submit()` on the pool

### BraveCoroutinesV1

* in this version we call `tracer.withSpanInScope(trace)` outside of coroutine code to make the trace current
* then we launch a coroutine with `kotlinx.coroutines.slf4j.MDCContext` added to the context
* this does not give us trace propagation, but at least MDC values from baggage are maintained between thread switches

### BraveCoroutinesV2

* this example improves the previous one by handling trace propagation within coroutines
* `TracingContextElement` is first implemented by extending `ThreadContextElement`
* we are then able to add `TracingContextElement(tracing.currentTraceContext())` to coroutine context
* this makes sure that tracing context is restored every time the coroutine switches a thread

### BraveReactorV1

* here we again rely on `io.micrometer:context-propagation` library
* `ContextRegistry.getInstance().registerThreadLocalAccessor()` is used to manipulate `tracing.currentTraceContext()`

## Links 

### Brave

* https://github.com/openzipkin/brave/tree/master/brave
* https://github.com/openzipkin/brave/issues/820