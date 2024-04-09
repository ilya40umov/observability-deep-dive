package me.ilya40umov.observability

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory

abstract class LogbackMdcBaseTest(
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

    protected fun ListAppender<ILoggingEvent>.allUniqueTraceIds(): Set<String> =
        list.mapNotNull { it.mdcPropertyMap["traceId"] }.toSet()

    protected fun ListAppender<ILoggingEvent>.findByMessage(message: String): ILoggingEvent =
        list.first { it.message == message }

    protected fun ListAppender<ILoggingEvent>.findByMessagePrefix(prefix: String): ILoggingEvent =
        list.first { it.message.startsWith(prefix) }

}