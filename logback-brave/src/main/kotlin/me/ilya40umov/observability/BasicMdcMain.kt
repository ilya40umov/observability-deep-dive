package me.ilya40umov.observability

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID

private val logger = LoggerFactory.getLogger("BasicMdcMain")

fun main() {
    MDC.put("traceId", UUID.randomUUID().toString())
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
    val previousContext = MDC.getCopyOfContextMap()
    MDC.put("traceId", UUID.randomUUID().toString())
    MDC.put("country", country)
    MDC.put("userId", userId)
    try {
        logger.info("Processing user data V1.")
    } finally {
        MDC.setContextMap(previousContext)
    }
}

private fun processUserData2(userId: String = "Stitch", country: String = "Outer Space") {
    val previousContext = MDC.getCopyOfContextMap()
    try {
        MDC.putCloseable("traceId", UUID.randomUUID().toString()).use {
            MDC.putCloseable("country", country).use {
                MDC.putCloseable("userId", userId).use {
                    logger.info("Processing user data V2.")
                }
            }
        }
    } finally {
        // XXX putCloseable() does not restore the previous value of the key!
        MDC.setContextMap(previousContext)
    }
}