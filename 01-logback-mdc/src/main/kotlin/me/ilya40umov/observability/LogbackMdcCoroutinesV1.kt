package me.ilya40umov.observability

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import me.ilya40umov.observability.helper.generateTraceId
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import kotlin.time.Duration.Companion.milliseconds

private val logger = LoggerFactory.getLogger("LogbackMdcCoroutinesV1")

fun main(): Unit = runBlocking {
    MDC.put("traceId", generateTraceId())
    logger.info("Entering main()")
    try {
        withContext(
            MDCContext(
                mapOf(
                    "traceId" to generateTraceId(),
                    "country" to "India",
                    "userId" to "Mahatma Gandhi"
                )
            )
        ) {
            launch(Dispatchers.Default) {
                delay(50.milliseconds)
                logger.info("Operation #1.")
            }
            launch(Dispatchers.Unconfined) {
                delay(100.milliseconds)
                logger.info("Operation #2.")
            }
        }
    } finally {
        logger.info("Leaving main()")
    }
}
