package me.ilya40umov.observability

import io.opentelemetry.api.baggage.Baggage
import io.opentelemetry.context.Context
import io.opentelemetry.instrumentation.reactor.v3_1.ContextPropagationOperator
import kotlinx.coroutines.runBlocking
import me.ilya40umov.observability.helper.openTelemetry
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.ParallelFlux
import reactor.core.scheduler.Schedulers
import java.time.Duration

private const val OTEL_REACTOR_V1 = "OtelReactorV1"
private val tracer = openTelemetry.getTracer(OTEL_REACTOR_V1)
private val logger = LoggerFactory.getLogger(OTEL_REACTOR_V1)

fun main(): Unit = runBlocking {
    ContextPropagationOperator.create().registerOnEachOperator()
    logger.info("Entering main()")
    try {
        val fluxes = listOf(
            UserData(userId = "Gandalf", country = "Middle Earth"),
            UserData(userId = "Saruman", country = "Middle Earth"),
            UserData(userId = "Galadriel", country = "Middle Earth"),
            UserData(userId = "Aratar", country = "Valinor"),
            UserData(userId = "Irmo", country = "Valinor"),
            UserData(userId = "Tulkas", country = "Valinor")
        ).map { (userId, country) ->
            val span = tracer.spanBuilder(OTEL_REACTOR_V1)
                .setAttribute("userId", userId)
                .setAttribute("country", country)
                .startSpan()
            val baggage = Baggage.builder()
                .put("userId", userId)
                .put("country", country)
                .build()
            val context = baggage.storeInContext(Context.current().with(span))
            Flux.just(UserData(userId, country))
                .subscribeOn(Schedulers.parallel())
                .doOnNext { logger.info("Processing - Phase 1.") }
                .delayElements(Duration.ofMillis(10))
                .doOnNext { logger.info("Processing - Phase 2.") }
                .delayElements(Duration.ofMillis(10))
                .doOnComplete { span.end() }
                .contextWrite { reactorContext ->
                    ContextPropagationOperator.storeOpenTelemetryContext(reactorContext, context)
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