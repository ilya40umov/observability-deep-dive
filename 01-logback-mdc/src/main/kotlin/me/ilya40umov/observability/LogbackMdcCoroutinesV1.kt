package me.ilya40umov.observability

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import me.ilya40umov.observability.helper.generateTraceId
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.milliseconds

const val LOGBACK_MDC_COROUTINES_V1 = "LogbackMdcCoroutinesV1"

private val logger = LoggerFactory.getLogger(LOGBACK_MDC_COROUTINES_V1)

fun logbackMdcCoroutinesV1(): Unit = runBlocking {
    logger.info("Entering main()")

    withContext(
        // MDCContext implements ThreadContextElement,
        //  which in turn implements CoroutineContext.Element
        MDCContext(
            mapOf(
                "traceId" to generateTraceId(),
                "country" to "Mars",
                "userId" to "Spike Spiegel"
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

    logger.info("Leaving main()")
}
