package me.ilya40umov.observability

import io.micrometer.context.ContextRegistry
import me.ilya40umov.observability.helper.generateTraceId
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import reactor.core.publisher.Flux
import reactor.core.publisher.Hooks
import reactor.core.scheduler.Schedulers
import java.time.Duration

private val logger = LoggerFactory.getLogger("LogbackMdcReactorV1")

fun main() {
    MDC.put("traceId", generateTraceId())
    logger.info("Entering main()")

    Hooks.enableAutomaticContextPropagation()
    listOf("traceId", "userId", "country").forEach { field ->
        ContextRegistry.getInstance()
            .registerThreadLocalAccessor(
                field,
                { MDC.get(field) },
                { MDC.put(field, it) },
                { MDC.remove(field) }
            )
    }

    try {
        Flux.just("Hello World!")
            .subscribeOn(Schedulers.parallel())
            .map { message ->
                logger.info("Phase 1: $message")
                "$message was processed."
            }
            .delayElements(Duration.ofMillis(10))
            .map { message ->
                logger.info("Phase 2: $message")
            }
            .contextWrite { context ->
                context
                    .put("traceId", generateTraceId())
                    .put("country", "Earth")
                    .put("userId", "Faye Valentine")
            }
            .blockLast()
    } finally {
        logger.info("Leaving main()")
    }
}