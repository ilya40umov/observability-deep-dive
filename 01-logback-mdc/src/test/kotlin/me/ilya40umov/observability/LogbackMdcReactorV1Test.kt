package me.ilya40umov.observability

import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LogbackMdcReactorV1Test : LogbackMdcBaseTest(loggerName = "LogbackMdcReactorV1") {

    @Test
    fun `logbackMdcReactorV1() should add all mdc fields for both messages`() {
        logbackMdcReactorV1()

        listOf("Phase 1:", "Phase 2:").forEach { prefix ->
            val event = appender.findByMessagePrefix(prefix)
            event.mdcPropertyMap shouldContainKey "traceId"
            event.mdcPropertyMap shouldContain ("country" to "Earth")
            event.mdcPropertyMap shouldContain ("userId" to "Faye Valentine")
        }
    }

}