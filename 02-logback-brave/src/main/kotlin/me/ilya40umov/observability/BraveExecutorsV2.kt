package me.ilya40umov.observability

import brave.baggage.BaggageField
import me.ilya40umov.observability.helper.TracingFactory
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val BRAVE_EXECUTORS_V2 = "BraveExecutorsV2"
private val countryBaggage = BaggageField.create("country")
private val userIdBaggage = BaggageField.create("userId")
private val tracing = TracingFactory.tracing(countryBaggage, userIdBaggage)
private val tracer = tracing.tracer()
private val logger = LoggerFactory.getLogger(BRAVE_EXECUTORS_V2)

fun main() {
    logger.info("Entering main()")

    val pool1 = tracing.currentTraceContext().executorService(
        Executors.newFixedThreadPool(4)
    )
    val pool2 = tracing.currentTraceContext().executorService(
        Executors.newFixedThreadPool(2)
    )

    listOf(
        UserData(userId = "The Little Prince", country = "Asteroid B 612"),
        UserData(userId = "The King", country = "Asteroid 325"),
        UserData(userId = "Vain Man", country = "Asteroid 326"),
        UserData(userId = "The Geographer", country = "Asteroid 330"),
        UserData(userId = "The Pilot", country = "Earth"),
        UserData(userId = "The Fox", country = "Earth")
    ).forEach { (userId, country) ->
        val trace = tracer.newTrace()
            .name(BRAVE_EXECUTORS_V2)
            .tag("userId", userId)
            .tag("country", country)
            .start()
        userIdBaggage.updateValue(trace.context(), userId)
        countryBaggage.updateValue(trace.context(), country)

        tracer.withSpanInScope(trace).use {
            pool1.submit {
                logger.info("Processing - Phase 1.")
                Thread.sleep(10L)
                pool2.submit {
                    logger.info("Processing - Phase 2.")
                    Thread.sleep(10L)
                    trace.finish()
                }
            }
        }
    }

    pool1.shutdown()
    pool1.awaitTermination(1, TimeUnit.MINUTES)
    pool2.shutdown()
    pool2.awaitTermination(1, TimeUnit.MINUTES)

    logger.info("Leaving main()")
}

