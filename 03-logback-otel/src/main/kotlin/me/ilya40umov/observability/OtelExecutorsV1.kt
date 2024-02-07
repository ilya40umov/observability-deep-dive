package me.ilya40umov.observability

import io.opentelemetry.api.baggage.Baggage
import io.opentelemetry.context.Context
import me.ilya40umov.observability.helper.openTelemetry
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val OTEL_EXECUTORS_V1 = "OtelExecutorsV1"
private val logger = LoggerFactory.getLogger(OTEL_EXECUTORS_V1)
private val tracer = openTelemetry.getTracer(OTEL_EXECUTORS_V1)

fun main() {
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
            val span = tracer.spanBuilder(OTEL_EXECUTORS_V1)
                .setAttribute("userId", userId)
                .setAttribute("country", country)
                .startSpan()
            val baggage = Baggage.builder()
                .put("userId", userId)
                .put("country", country)
                .build()
            val context = Context.current().with(span)
            baggage.storeInContext(context).makeCurrent().use {
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
        pool1.shutdown()
        pool1.awaitTermination(1, TimeUnit.MINUTES)

        pool2.shutdown()
        pool2.awaitTermination(1, TimeUnit.MINUTES)
    } finally {
        logger.info("Leaving main()")
    }
}

