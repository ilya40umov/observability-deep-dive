package me.ilya40umov.observability

import brave.baggage.BaggageField
import me.ilya40umov.observability.helper.TracingFactory
import org.slf4j.LoggerFactory

const val BRAVE_HELLO_WORLD_V1 = "BraveHelloWorldV1"

private val userIdBaggage = BaggageField.create("userId")
private val tracer = TracingFactory.tracing(userIdBaggage).tracer()
private val logger = LoggerFactory.getLogger(BRAVE_HELLO_WORLD_V1)

fun braveHelloWorldV1() {
    logger.info("Entering main()")

    val trace = tracer.newTrace()
        .name(BRAVE_HELLO_WORLD_V1)
        .tag("userId", "Yoda")
        .start()
    userIdBaggage.updateValue(trace.context(), "Yoda")
    tracer.withSpanInScope(trace).use {
        logger.info("Patience you must have, my young Padawan.")
    }
    trace.finish()

    logger.info("Leaving main()")
}
