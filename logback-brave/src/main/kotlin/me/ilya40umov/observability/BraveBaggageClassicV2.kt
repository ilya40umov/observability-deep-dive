package me.ilya40umov.observability

import brave.baggage.BaggageField
import me.ilya40umov.observability.helpers.TracingFactory
import me.ilya40umov.observability.models.UserData
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val countryBaggage = BaggageField.create("country")
private val userIdBaggage = BaggageField.create("userId")

private val tracing = TracingFactory.tracing(countryBaggage, userIdBaggage)
private val tracer = tracing.tracer()

private val logger = LoggerFactory.getLogger("BraveBaggageClassicV2")

fun main() {
    logger.info("Entering main()")
    try {
        val pool1 = tracing.currentTraceContext().executorService(
            Executors.newFixedThreadPool(4)
        )
        val pool2 = tracing.currentTraceContext().executorService(
            Executors.newFixedThreadPool(2)
        )

        listOf(
            UserData(userId = "SpacePirate1", country = "Inner Space"),
            UserData(userId = "SpacePirate2", country = "Inner Space"),
            UserData(userId = "SpacePirate3", country = "Inner Space"),
            UserData(userId = "Stitch1", country = "Outer Space"),
            UserData(userId = "Stitch2", country = "Outer Space"),
            UserData(userId = "Stitch3", country = "Outer Space")
        ).forEach { (userId, country) ->
            val trace = tracer.newTrace()
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
    } finally {
        logger.info("Leaving main()")
    }
}

