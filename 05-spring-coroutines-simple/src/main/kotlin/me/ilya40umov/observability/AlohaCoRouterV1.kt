@file:Suppress("DuplicatedCode")

package me.ilya40umov.observability

import me.ilya40umov.observability.model.UserData
import me.ilya40umov.observability.service.HelloService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.attributeOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AlohaCoRouterV1(
    private val helloService: HelloService
) {
    @Bean
    fun alohaV1Route() = coRouter {
        GET("/v1/aloha") { request ->
            val user = request.attributeOrNull(USER_ATTRIBUTE) as UserData
            val greeting = helloService.sayHelloTo(user)
            ServerResponse.ok().bodyValueAndAwait(greeting)
        }
    }
}