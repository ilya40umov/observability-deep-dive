package me.ilya40umov.observability

import io.opentelemetry.api.baggage.Baggage
import io.opentelemetry.context.Context
import me.ilya40umov.observability.helper.openTelemetry
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("OtelHelloWorldV1")

fun main() {
    val tracer = openTelemetry.getTracer("OtelHelloWorldV1")
    val user = UserData(userId = "Zorro", country = "Mexico")
    val span = tracer.spanBuilder("main")
        .setAttribute("userId", user.userId)
        .setAttribute("country", user.country)
        .startSpan()
    try {
        span.makeCurrent().use {
            val baggage = Baggage.builder()
                .put("userId", user.userId)
                .put("country", user.country)
                .build()
            baggage.storeInContext(Context.current()).makeCurrent().use {
                logger.info("Hello ${user.userId}!")
                logger.warn("Or perhaps Don Diego de la Vega? ;)")
                logger.info("Bye.")
            }
        }
    } finally {
        span.end()
    }
}