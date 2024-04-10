package me.ilya40umov.observability

import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BraveExecutorsV1Test : BraveBaseTest(loggerName = BRAVE_EXECUTORS_V1) {

    @Test
    fun `braveExecutorsV1() should add traceId, userId and country to MDC`() {
        braveExecutorsV1()

        appender.findAllByMessage("Processing - Phase 1.").also { events ->
            events.getUniqueValuesUnderMdcKey("traceId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("country") shouldHaveSize 4
        }
        appender.findAllByMessage("Processing - Phase 2.").also { events ->
            events.getUniqueValuesUnderMdcKey("traceId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("country") shouldHaveSize 4
        }
    }

}