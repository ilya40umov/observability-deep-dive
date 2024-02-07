package me.ilya40umov.observability

import io.micrometer.tracing.BaggageInScope
import io.micrometer.tracing.Tracer
import me.ilya40umov.observability.model.Greeting
import me.ilya40umov.observability.model.UserData
import me.ilya40umov.observability.service.HelloService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloControllerV2(
    private val helloService: HelloService,
    private val tracer: Tracer
) {

    @GetMapping("/v2/hello")
    fun hello(): Greeting {
        val user = UserData(userId = "Gandalf", country = "Undying Lands")
        val baggage = listOf(
            tracer.createBaggageInScope("userId", user.userId),
            tracer.createBaggageInScope("country", user.country)
        )
        return try {
            helloService.sayHelloTo(user)
        } finally {
            baggage.forEach(BaggageInScope::close)
        }
    }
}