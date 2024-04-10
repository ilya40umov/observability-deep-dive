package me.ilya40umov.observability

import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BraveExecutorsV2Test : BraveBaseTest(loggerName = BRAVE_EXECUTORS_V2) {

    @Test
    fun `braveExecutorsV2() should add traceId, userId and country to MDC`() {
        braveExecutorsV2()

        appender.findAllByMessage("Processing - Phase 1.").also { events ->
            events.getUniqueValuesUnderMdcKey("traceId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("country") shouldHaveSize 5
        }
        appender.findAllByMessage("Processing - Phase 2.").also { events ->
            events.getUniqueValuesUnderMdcKey("traceId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("country") shouldHaveSize 5
        }
    }

}