package me.ilya40umov.observability

import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BraveHelloWorldV1Test : BraveBaseTest(loggerName = BRAVE_HELLO_WORLD_V1) {

    @Test
    fun `braveHelloWorldV1() should add traceId and userId to Yoda's message`() {
        braveHelloWorldV1()

        val message = appender.findFirstByMessage("Patience you must have, my young Padawan.")
        message.mdcPropertyMap shouldContainKey "traceId"
        message.mdcPropertyMap shouldContain  ("userId" to "Yoda")
    }

}