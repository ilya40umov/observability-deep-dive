package me.ilya40umov.observability

import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LogbackHelloWorldV1Test : LogbackMdcBaseTest(loggerName = "LogbackHelloWorldV1") {

    @Test
    fun `logbackHelloWorldV1() should add traceId and userId to Arthur's message`() {
        logbackHelloWorldV1()

        val event = appender.findByMessage("Is there any tea on this spaceship?")
        event.mdcPropertyMap shouldContainKey "traceId"
        event.mdcPropertyMap shouldContain ("userId" to "Arthur Dent")
    }
}