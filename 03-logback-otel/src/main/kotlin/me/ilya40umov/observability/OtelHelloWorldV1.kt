package me.ilya40umov.observability

import io.opentelemetry.api.baggage.Baggage
import io.opentelemetry.context.Context
import me.ilya40umov.observability.helper.openTelemetry
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory

private const val OTEL_HELLO_WORLD_V1 = "OtelHelloWorldV1"
private val logger = LoggerFactory.getLogger(OTEL_HELLO_WORLD_V1)
private val tracer = openTelemetry.getTracer(OTEL_HELLO_WORLD_V1)

fun main() {
    val user = UserData(userId = "Zorro", country = "Mexico")
    val span = tracer.spanBuilder(OTEL_HELLO_WORLD_V1)
        .setAttribute("userId", user.userId)
        .setAttribute("country", user.country)
        .startSpan()
    val baggage = Baggage.builder()
        .put("userId", user.userId)
        .put("country", user.country)
        .build()
    try {
        val context = Context.current().with(span)
        baggage.storeInContext(context).makeCurrent().use {
            logger.info("Hello ${user.userId}!")
            Thread.sleep(10)
            logger.warn("Or perhaps Don Diego de la Vega? ;)")
            Thread.sleep(10)
            logger.info("Bye.")
        }
    } finally {
        span.end()
    }
}