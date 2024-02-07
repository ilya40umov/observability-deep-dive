package me.ilya40umov.observability

import io.opentelemetry.api.baggage.Baggage
import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.ilya40umov.observability.helper.openTelemetry
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory

private const val OTEL_COROUTINES_V1 = "OtelCoroutinesV1"
private val tracer = openTelemetry.getTracer(OTEL_COROUTINES_V1)
private val logger = LoggerFactory.getLogger(OTEL_COROUTINES_V1)

fun main(): Unit = runBlocking {

    logger.info("Entering main()")
    try {
        val coroutineScope = CoroutineScope(Dispatchers.Default)

        val jobs = listOf(
            UserData(userId = "Dwalin", country = "Erebor"),
            UserData(userId = "Fili", country = "Erebor"),
            UserData(userId = "Kili", country = "Erebor"),
            UserData(userId = "Bandobras", country = "The Shire"),
            UserData(userId = "Posco", country = "The Shire"),
            UserData(userId = "Merry", country = "The Shire")
        ).map { (userId, country) ->
            val span = tracer.spanBuilder(OTEL_COROUTINES_V1)
                .setAttribute("userId", userId)
                .setAttribute("country", country)
                .startSpan()
            val baggage = Baggage.builder()
                .put("userId", userId)
                .put("country", country)
                .build()
            val context = baggage.storeInContext(Context.current().with(span))
            coroutineScope.launch(context.asContextElement()) {
                logger.info("Processing - Phase 1 (before delay).")
                delay(10)
                logger.info("Processing - Phase 1 (after delay).")
                launch {
                    logger.info("Processing - Phase 2.")
                    delay(10)
                    span.end()
                }
            }
        }

        jobs.forEach { it.join() }
    } finally {
        logger.info("Leaving main()")
    }
}