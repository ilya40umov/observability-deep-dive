package me.ilya40umov.observability

import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class OtelHelloWorldV1Test : OtelBaseTest(loggerName = OTEL_HELLO_WORLD_V1) {

    @Test
    fun `otelHelloWorldV1() should add traceId and userId to log message about Zorro`() {
        otelHelloWorldV1()

        val message = appender.findFirstByMessage("Hello Zorro!")
        message.mdcPropertyMap shouldContainKey "trace_id"
        message.mdcPropertyMap shouldContain  ("baggage.userId" to "Zorro")
    }

}