package me.ilya40umov.observability

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LogbackMdcClassicV1Test : LogbackBaseTest(loggerName = LOGBACK_MDC_CLASSIC_V1) {

    @Test
    fun `logbackMdcClassicV1() should add all mdc fields for first Lilo message`() {
        logbackMdcClassicV1()

        val event = appender.findByMessage("Starting to process user data.")
        event.mdcPropertyMap shouldContainKey "traceId"
        event.mdcPropertyMap shouldContain ("country" to "Inner Space")
        event.mdcPropertyMap shouldContain ("userId" to "Lilo")
    }

    @Test
    fun `logbackMdcClassicV1() should change mdc fields for Stitch message`() {
        logbackMdcClassicV1()

        val event = appender.findByMessage("Processing user data V2.")
        event.mdcPropertyMap shouldContainKey "traceId"
        event.mdcPropertyMap shouldContain ("country" to "Outer Space")
        event.mdcPropertyMap shouldContain ("userId" to "Stitch")
    }

    @Test
    fun `logbackMdcClassicV1() should restore mdc fields for last Lilo message`() {
        logbackMdcClassicV1()

        val event = appender.findByMessage("Finishing to process user data.")
        event.mdcPropertyMap shouldContainKey "traceId"
        event.mdcPropertyMap shouldContain ("country" to "Inner Space")
        event.mdcPropertyMap shouldContain ("userId" to "Lilo")
    }

    @Test
    fun `logbackMdcClassicV1() should only record two unique trace ID values`() {
        logbackMdcClassicV1()

        appender.allUniqueTraceIds() shouldHaveSize 2
    }

}