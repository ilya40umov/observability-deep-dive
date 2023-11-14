package me.ilya40umov.observability.api

import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    private val logger = LoggerFactory.getLogger(HelloController::class.java)

    data class Greeting(
        val message: String
    )

    @GetMapping("/api/hello")
    suspend fun hello(): Greeting {
        // MDC.put("userId", "Frodo")
        logger.info("hello() was called")
        // MDC.remove("userId")
        return Greeting(message = "Hello World!")
    }
}