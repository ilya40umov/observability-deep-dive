package me.ilya40umov.observability

import me.ilya40umov.observability.model.Greeting
import me.ilya40umov.observability.model.UserData
import me.ilya40umov.observability.service.HelloService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloControllerV1(
    private val helloService: HelloService
) {
    @GetMapping("/v1/hello")
    suspend fun hello(@RequestAttribute(USER_ATTRIBUTE) user: UserData): Greeting {
        return helloService.sayHelloTo(user)
    }
}