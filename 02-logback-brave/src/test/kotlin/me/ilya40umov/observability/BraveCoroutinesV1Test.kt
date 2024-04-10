package me.ilya40umov.observability

import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BraveCoroutinesV1Test : BraveBaseTest(loggerName = BRAVE_COROUTINES_V1) {

    @Test
    fun `braveCoroutinesV1() should add traceId, userId and country to MDC`() {
        braveCoroutinesV1()

        appender.findAllByMessage("Processing - Phase 1 (after delay).").also { events ->
            events.getUniqueValuesUnderMdcKey("traceId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("country") shouldHaveSize 2
        }
        appender.findAllByMessage("Processing - Phase 2.").also { events ->
            events.getUniqueValuesUnderMdcKey("traceId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("country") shouldHaveSize 2
        }
    }

}