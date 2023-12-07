package me.ilya40umov.observability

import me.ilya40umov.observability.models.UserData
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID

private val logger = LoggerFactory.getLogger("LogbackMdcClassicV1")

fun main() {
    MDC.put("traceId", UUID.randomUUID().toString())
    logger.info("Entering main()")
    try {
        processUserDataV1(UserData(userId = "Incognito", country = "Inner Space"))
        logger.info("Going for another user.")
        processUserDataV2(UserData(userId = "Stitch", country = "Outer Space"))
    } finally {
        logger.info("Leaving main()")
    }
}

private fun processUserDataV1(user: UserData) {
    val previousContext = MDC.getCopyOfContextMap()
    MDC.put("traceId", UUID.randomUUID().toString())
    MDC.put("country", user.country)
    MDC.put("userId", user.userId)
    try {
        logger.info("Processing user data V1.")
    } finally {
        // XXX if we were just to call remove() for all keys,
        //  we would lose the previous values stored in MDC
        MDC.setContextMap(previousContext)
    }
}

private fun processUserDataV2(user: UserData) {
    val previousContext = MDC.getCopyOfContextMap()
    try {
        MDC.putCloseable("traceId", UUID.randomUUID().toString()).use {
            MDC.putCloseable("country", user.country).use {
                MDC.putCloseable("userId", user.userId).use {
                    logger.info("Processing user data V2.")
                }
            }
        }
    } finally {
        // XXX putCloseable() also does not restore the previous value of the key!
        MDC.setContextMap(previousContext)
    }
}