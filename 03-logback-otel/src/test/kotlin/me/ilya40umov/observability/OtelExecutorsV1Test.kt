package me.ilya40umov.observability

import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class OtelExecutorsV1Test : OtelBaseTest(loggerName = OTEL_EXECUTORS_V1) {

    @Test
    fun `otelExecutorsV1() should add traceId, userId and country to MDC`() {
        otelExecutorsV1()

        appender.findAllByMessage("Processing - Phase 1.").also { events ->
            events.getUniqueValuesUnderMdcKey("trace_id") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("baggage.userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("baggage.country") shouldHaveSize 5
        }
        appender.findAllByMessage("Processing - Phase 2.").also { events ->
            events.getUniqueValuesUnderMdcKey("trace_id") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("baggage.userId") shouldHaveSize 6
            events.getUniqueValuesUnderMdcKey("baggage.country") shouldHaveSize 5
        }
    }

}