package me.ilya40umov.observability

import brave.Span
import brave.baggage.BaggageField
import me.ilya40umov.observability.helper.TracingFactory
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val countryBaggage = BaggageField.create("country")
private val userIdBaggage = BaggageField.create("userId")

private val tracer = TracingFactory.tracing(countryBaggage, userIdBaggage).tracer()

private val logger = LoggerFactory.getLogger("BraveBaggageClassicV1")

fun main() {
    logger.info("Entering main()")
    try {
        val pool1 = Executors.newFixedThreadPool(2)
        val pool2 = Executors.newFixedThreadPool(4)

        listOf(
            UserData(userId = "Edward Elric", country = "Amestris"),
            UserData(userId = "Alphonse Elric", country = "Amestris"),
            UserData(userId = "Van Hohenheim", country = "Xerxes"),
            UserData(userId = "Scar", country = "Ishval"),
            UserData(userId = "Ling Yao", country = "Xing"),
            UserData(userId = "Mei Chang", country = "Xing")
        ).forEach { (userId, country) ->
            val trace = tracer.newTrace()
                .name("BraveClassicV1")
                .tag("userId", userId)
                .tag("country", country)
                .start()
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
