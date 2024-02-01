package me.ilya40umov.observability

import me.ilya40umov.observability.service.LoggingService
import me.ilya40umov.observability.model.Greeting
import org.slf4j.MDC
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoggingControllerV1(
    private val loggingService: LoggingService
) {

    @GetMapping("/v1/hello")
    fun hello(): Greeting {
        MDC.put("country", "Shire")
        MDC.put("userId", "Frodo")
        try {
            loggingService.method1()
        } finally {
            MDC.remove("userId")
            MDC.remove("country")
        }
        return Greeting()
    }

}