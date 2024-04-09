package me.ilya40umov.observability

import me.ilya40umov.observability.helper.generateTraceId
import org.slf4j.LoggerFactory
import org.slf4j.MDC

private val logger = LoggerFactory.getLogger("LogbackHelloWorldV1")

fun logbackHelloWorldV1() {
    logger.info("Entering main()")

    MDC.put("traceId", generateTraceId())
    MDC.put("userId", "Arthur Dent")
    logger.info("Is there any tea on this spaceship?")
    MDC.clear()

    logger.info("Leaving main()")
}