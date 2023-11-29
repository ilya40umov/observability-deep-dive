package me.ilya40umov.observability

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID

private val logger = LoggerFactory.getLogger("LogbackMdcMain")

fun main() {
    logger.info("Entering main()")
    try {
        processUserData1()
        logger.info("Going for another user.")
        processUserData2()
    } finally {
        logger.info("Leaving main()")
    }
}

private fun processUserData1(userId: String = "Incognito", country: String = "Inner Space") {
    MDC.put("traceId", UUID.randomUUID().toString())
    MDC.put("country", country)
    MDC.put("userId", userId)
    try {
        logger.info("Processing user data V1.")
    } finally {
        MDC.remove("userId")
        MDC.remove("country")
        MDC.remove("traceId")
    }
}

private fun processUserData2(userId: String = "Stitch", country: String = "Outer Space") {
    MDC.putCloseable("traceId", UUID.randomUUID().toString()).use {
        MDC.putCloseable("country", country).use {
            MDC.putCloseable("userId", userId).use {
                logger.info("Processing user data V2.")
            }
        }
    }
}