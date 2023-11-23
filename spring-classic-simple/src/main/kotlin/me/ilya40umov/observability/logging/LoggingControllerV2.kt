package me.ilya40umov.observability.logging

import io.micrometer.tracing.BaggageInScope
import io.micrometer.tracing.Tracer
import me.ilya40umov.observability.Greeting
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoggingControllerV2(
    private val loggingService: LoggingService,
    private val tracer: Tracer
) {

    @GetMapping("/v2/hello")
    fun hello(): Greeting {
        val baggage = listOf(
            tracer.createBaggageInScope("userId", "Gandalf"),
            tracer.createBaggageInScope("country", "Undying Lands")
        )
        try {
            loggingService.method1()
        } finally {
            baggage.forEach(BaggageInScope::close)
        }
        return Greeting()
    }
}