package me.ilya40umov.observability

import brave.baggage.BaggageField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import me.ilya40umov.observability.helper.TracingFactory
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory

private val countryBaggage = BaggageField.create("country")
private val userIdBaggage = BaggageField.create("userId")

private val tracing = TracingFactory.tracing(countryBaggage, userIdBaggage)
private val tracer = tracing.tracer()

private val logger = LoggerFactory.getLogger("BraveBaggageCoroutinesV1")

fun main() = runBlocking {
    logger.info("Entering main()")
    try {
        val coroutineScope = CoroutineScope(Dispatchers.Default)

        val jobs = listOf(
            UserData(userId = "Bifur", country = "Erebor"),
            UserData(userId = "Dori", country = "Erebor"),
            UserData(userId = "Ori", country = "Erebor"),
            UserData(userId = "Bilbo", country = "The Shire"),
            UserData(userId = "Frodo", country = "The Shire"),
            UserData(userId = "Pippin", country = "The Shire")
        ).map { (userId, country) ->
            val trace = tracer.newTrace()
                .name("BraveCoroutinesV1")
                .tag("userId", userId)
                .tag("country", country)
                .start()
            userIdBaggage.updateValue(trace.context(), userId)
            countryBaggage.updateValue(trace.context(), country)

            tracer.withSpanInScope(trace).use {
                coroutineScope.launch(
                    MDCContext()
                ) {
                    // XXX however, this also means that span-in-scope is not propagated, just MDC fields
                    logger.info("Processing - Phase 1 (before delay).")
                    delay(10)
                    logger.info("Processing - Phase 1 (after delay).")
                    launch { // inherits scope from the calling coroutine
                        logger.info("Processing - Phase 2.")
                        delay(10)
                        trace.finish()
                    }
                }
            }
        }

        jobs.forEach { it.join() }
    } finally {
        logger.info("Leaving main()")
    }
}