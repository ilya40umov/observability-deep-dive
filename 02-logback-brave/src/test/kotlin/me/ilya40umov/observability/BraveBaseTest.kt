package me.ilya40umov.observability

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory

abstract class BraveBaseTest(
    loggerName: String,
) {
    protected val appender = ListAppender<ILoggingEvent>()
    private val logger = LoggerFactory.getLogger(loggerName) as Logger

    @BeforeEach
    fun setUp() {
        appender.start()
        logger.addAppender(appender)
    }

    @AfterEach
    fun tearDown() {
        logger.detachAppender(appender)
    }

    protected fun List<ILoggingEvent>.getUniqueValuesUnderMdcKey(key: String): Set<String> =
        mapNotNull { it.mdcPropertyMap[key] }.toSet()

    protected fun ListAppender<ILoggingEvent>.findFirstByMessage(message: String): ILoggingEvent =
        list.first { it.message == message }

    protected fun ListAppender<ILoggingEvent>.findAllByMessage(message: String): List<ILoggingEvent> =
        list.filter { it.message == message }
}