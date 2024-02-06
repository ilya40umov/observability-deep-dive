package me.ilya40umov.observability

import io.opentelemetry.api.baggage.Baggage
import io.opentelemetry.context.Context
import me.ilya40umov.observability.helper.openTelemetry
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val logger = LoggerFactory.getLogger("OtelBaggageClassicV1")

fun main() {
    val tracer = openTelemetry.getTracer("OtelBaggageClassicV1")
    logger.info("Entering main()")
    try {
        val pool1 = Context.taskWrapping(Executors.newFixedThreadPool(4))
        val pool2 = Context.taskWrapping(Executors.newFixedThreadPool(2))

        listOf(
            UserData(userId = "The Little Prince", country = "Asteroid B 612"),
            UserData(userId = "The King", country = "Asteroid 325"),
            UserData(userId = "Vain Man", country = "Asteroid 326"),
            UserData(userId = "The Geographer", country = "Asteroid 330"),
            UserData(userId = "The Pilot", country = "Earth"),
            UserData(userId = "The Fox", country = "Earth")
        ).forEach { (userId, country) ->
            val span = tracer.spanBuilder("OtelClassicV1")
                .setAttribute("userId", userId)
                .setAttribute("country", country)
                .startSpan()

            span.makeCurrent().use {
                val baggage = Baggage.builder()
                    .put("userId", userId)
                    .put("country", country)
                    .build()
                baggage.storeInContext(Context.current()).makeCurrent().use {
                    pool1.submit {
                        logger.info("Processing - Phase 1.")
                        Thread.sleep(10L)
                        pool2.submit {
                            logger.info("Processing - Phase 2.")
                            Thread.sleep(10L)
                            span.end()
                        }
                    }
                }
            }
        }
        pool1.shutdown()
        pool1.awaitTermination(1, TimeUnit.MINUTES)

        pool2.shutdown()
        pool2.awaitTermination(1, TimeUnit.MINUTES)
    } finally {
        logger.info("Leaving main()")
    }
}

