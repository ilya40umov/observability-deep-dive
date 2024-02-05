package me.ilya40umov.observability

import brave.baggage.BaggageField
import io.micrometer.context.ContextRegistry
import kotlinx.coroutines.runBlocking
import me.ilya40umov.observability.helper.TracingFactory
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Hooks
import reactor.core.publisher.ParallelFlux
import reactor.core.scheduler.Schedulers
import java.time.Duration

private val countryBaggage = BaggageField.create("country")
private val userIdBaggage = BaggageField.create("userId")

private val tracing = TracingFactory.tracing(countryBaggage, userIdBaggage)
private val tracer = tracing.tracer()

private val logger = LoggerFactory.getLogger("BraveBaggageReactorV1")

fun main(): Unit = runBlocking {
    logger.info("Entering main()")

    Hooks.enableAutomaticContextPropagation()
    // XXX this seems to work for this simple scenario,
    //  however to get to a more proper solution it's better to rewrite it to look like ObservationThreadLocalAccessor
    ContextRegistry.getInstance()
        .registerThreadLocalAccessor(
            "brave-trace-context",
            { tracing.currentTraceContext().get() },
            { tracing.currentTraceContext().maybeScope(it) },
            { tracing.currentTraceContext().newScope(null) }
        )

    try {
        val fluxes = listOf(
            UserData(userId = "Gandalf", country = "Middle Earth"),
            UserData(userId = "Saruman", country = "Middle Earth"),
            UserData(userId = "Galadriel", country = "Middle Earth"),
            UserData(userId = "Aratar", country = "Valinor"),
            UserData(userId = "Irmo", country = "Valinor"),
            UserData(userId = "Tulkas", country = "Valinor")
        ).map { (userId, country) ->
            val trace = tracer.newTrace()
                .name("BraveReactorV1")
                .tag("userId", userId)
                .tag("country", country)
                .start()
            userIdBaggage.updateValue(trace.context(), userId)
            countryBaggage.updateValue(trace.context(), country)

            tracer.withSpanInScope(trace).use {
                val braveTraceContext = tracing.currentTraceContext().get()
                Flux.just(UserData(userId, country))
                    .subscribeOn(Schedulers.parallel())
                    .doOnNext { logger.info("Processing - Phase 1.") }
                    .delayElements(Duration.ofMillis(10))
                    .doOnNext { logger.info("Processing - Phase 2.") }
                    .delayElements(Duration.ofMillis(10))
                    .doOnComplete { trace.finish() }
                    .contextWrite { context ->
                        context.put("brave-trace-context", braveTraceContext)
                    }
            }
        }
        ParallelFlux
            .from(*fluxes.toTypedArray())
            .then()
            .block()
    } finally {
        logger.info("Leaving main()")
    }
}