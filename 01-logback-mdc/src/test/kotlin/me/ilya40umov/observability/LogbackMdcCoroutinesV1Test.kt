package me.ilya40umov.observability

import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LogbackMdcCoroutinesV1Test : LogbackMdcBaseTest(loggerName = "LogbackMdcCoroutinesV1") {

    @Test
    fun `logbackMdcCoroutinesV1() should add all mdc fields for both messages`() {
        logbackMdcCoroutinesV1()

        listOf("Operation #1.", "Operation #2.").forEach { message ->
            val event = appender.findByMessage(message)
            event.mdcPropertyMap shouldContainKey "traceId"
            event.mdcPropertyMap shouldContain ("country" to "Mars")
            event.mdcPropertyMap shouldContain ("userId" to "Spike Spiegel")
        }
    }

}