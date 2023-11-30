package me.ilya40umov.observability

import brave.baggage.BaggageField
import me.ilya40umov.observability.helpers.TracingFactory
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val countryBaggage = BaggageField.create("country")
private val userIdBaggage = BaggageField.create("userId")

private val tracer = TracingFactory.tracing(countryBaggage, userIdBaggage).tracer()

private val logger = LoggerFactory.getLogger("BraveBaggageV1Main")

fun main() {
    data class UserData(
        val userId: String,
        val country: String
    )

    logger.info("Entering main()")
    try {
        val pool1 = Executors.newFixedThreadPool(2)
        val pool2 = Executors.newFixedThreadPool(4)

        listOf(
            UserData(userId = "Incognito1", country = "Inner Space"),
            UserData(userId = "Incognito2", country = "Inner Space"),
            UserData(userId = "Incognito3", country = "Inner Space"),
            UserData(userId = "Stitch1", country = "Outer Space"),
            UserData(userId = "Stitch2", country = "Outer Space"),
            UserData(userId = "Stitch3", country = "Outer Space")
        ).forEach { (userId, country) ->
            val trace = tracer.newTrace()
            userIdBaggage.updateValue(trace.context(), userId)
            countryBaggage.updateValue(trace.context(), country)

            pool1.submit {
                tracer.withSpanInScope(trace).use {
                    logger.info("Processing - Phase 1.")
                }
                Thread.sleep(10L)
                pool2.submit {
                    tracer.withSpanInScope(trace).use {
                        logger.info("Processing - Phase 2.")
                    }
                    trace.finish()
                }
                Thread.sleep(10L)
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
