package me.ilya40umov.observability

import io.opentelemetry.api.baggage.Baggage
import io.opentelemetry.context.Context
import me.ilya40umov.observability.helper.openTelemetry
import org.slf4j.LoggerFactory

private const val OTEL_HELLO_WORLD_V1 = "OtelHelloWorldV1"
private val logger = LoggerFactory.getLogger(OTEL_HELLO_WORLD_V1)
private val tracer = openTelemetry.getTracer(OTEL_HELLO_WORLD_V1)

fun main() {
    logger.info("Entering main()")

    val span = tracer.spanBuilder(OTEL_HELLO_WORLD_V1)
        .setAttribute("userId", "Zorro")
        .startSpan()
    val baggage = Baggage.builder()
        .put("userId", "Zorro")
        .build()

    val context = Context.current().with(span)
    baggage.storeInContext(context).makeCurrent().use {
        logger.info("Hello Zorro!")
        Thread.sleep(10)
        logger.warn("Or perhaps Don Diego de la Vega? ;)")
        Thread.sleep(10)
        logger.info("Bye.")
    }
    span.end()

    logger.info("Leaving main()")
}