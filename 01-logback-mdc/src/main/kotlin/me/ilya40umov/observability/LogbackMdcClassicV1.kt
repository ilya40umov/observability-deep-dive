package me.ilya40umov.observability

import me.ilya40umov.observability.helper.generateTraceId
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import org.slf4j.MDC

const val LOGBACK_MDC_CLASSIC_V1 = "LogbackMdcClassicV1"

private val logger = LoggerFactory.getLogger(LOGBACK_MDC_CLASSIC_V1)

fun logbackMdcClassicV1() {
    logger.info("Entering main()")
    processUserDataV1(UserData(userId = "Lilo", country = "Inner Space"))
    logger.info("Leaving main()")
}

private fun processUserDataV1(user: UserData) {
    val previousContext = MDC.getCopyOfContextMap()
    MDC.put("traceId", generateTraceId())
    MDC.put("country", user.country)
    MDC.put("userId", user.userId)
    try {
        logger.info("Starting to process user data.")
        logger.info("Needing to handle another user first.")
        processUserDataV2(UserData(userId = "Stitch", country = "Outer Space"))
        logger.info("Finishing to process user data.")
    } finally {
        // XXX if we were just to call remove() for all keys,
        //  we would lose the previous values stored in MDC
        MDC.setContextMap(previousContext)
    }
}

private fun processUserDataV2(user: UserData) {
    val previousContext = MDC.getCopyOfContextMap()
    try {
        MDC.putCloseable("traceId", generateTraceId()).use {
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