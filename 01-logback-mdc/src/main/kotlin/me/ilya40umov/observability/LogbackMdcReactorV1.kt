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
    logger.info("Entering main()")

    Hooks.enableAutomaticContextPropagation()
    // TODO migrate to Slf4jThreadLocalAccessor?
    // https://github.com/micrometer-metrics/context-propagation/commit/c1d2fd41bfc56d33ddc5e844f2a9fba4e82ecf15
    listOf("traceId", "userId", "country").forEach { field ->
        ContextRegistry.getInstance()
            .registerThreadLocalAccessor(
                field,
                { MDC.get(field) }, // getSupplier
                { MDC.put(field, it) }, // setConsumer
                { MDC.remove(field) } // resetTask
            )
    }

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

    logger.info("Leaving main()")
}