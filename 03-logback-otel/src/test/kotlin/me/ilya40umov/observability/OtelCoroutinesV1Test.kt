package me.ilya40umov.observability

import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class OtelCoroutinesV1Test : OtelBaseTest(loggerName = OTEL_COROUTINES_V1) {

    @Test
    fun `otelCoroutinesV1() should add traceId, userId and country to MDC`() {
        otelCoroutinesV1()

        appender.findAllByMessage("Processing - Phase 1 (after delay).").also { events ->
            events.getUniqueValuesUnderMdcKey("trace_id") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("baggage.userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("baggage.country") shouldHaveSize 2
        }
        appender.findAllByMessage("Processing - Phase 2.").also { events ->
            events.getUniqueValuesUnderMdcKey("trace_id") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("baggage.userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("baggage.country") shouldHaveSize 2
        }
    }

}