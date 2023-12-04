package me.ilya40umov.observability

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

private val logger = LoggerFactory.getLogger("CoroutinesMdcMain")

fun main(): Unit = runBlocking {
    MDC.put("traceId", UUID.randomUUID().toString())
    logger.info("Entering main()")
    try {
        withContext(
            MDCContext(
                mapOf(
                    "traceId" to UUID.randomUUID().toString(),
                    "country" to "India",
                    "userId" to "Mahatma Gandhi"
                )
            )
        ) {
            launch(Dispatchers.Default) {
                delay(50.milliseconds)
                logger.info("Operation #1.")
            }
            launch(Dispatchers.IO) {
                delay(100.milliseconds)
                logger.info("Operation #2.")
            }
        }
    } finally {
        logger.info("Leaving main()")
    }
}
