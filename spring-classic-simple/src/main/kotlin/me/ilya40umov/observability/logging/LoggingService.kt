package me.ilya40umov.observability.logging

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LoggingService {

    private val logger = LoggerFactory.getLogger(LoggingService::class.java)

    fun method1() {
        logger.info("entering method1()")
        method2()
        logger.info("leaving method1()")
    }

    private fun method2() {
        logger.info("entering method2()")
        logger.info("leaving method2()")
    }
}