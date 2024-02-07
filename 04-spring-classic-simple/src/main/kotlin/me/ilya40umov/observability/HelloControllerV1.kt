package me.ilya40umov.observability

import me.ilya40umov.observability.model.Greeting
import me.ilya40umov.observability.model.UserData
import me.ilya40umov.observability.service.HelloService
import org.slf4j.MDC
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloControllerV1(
    private val helloService: HelloService
) {

    @GetMapping("/v1/hello")
    fun hello(): Greeting {
        val user = UserData(userId = "Frodo", country = "Shire")
        MDC.put("country", user.country)
        MDC.put("userId", user.userId)
        return try {
            helloService.sayHelloTo(user)
        } finally {
            MDC.remove("userId")
            MDC.remove("country")
        }
    }
}