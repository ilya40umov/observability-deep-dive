@file:Suppress("DuplicatedCode")

package me.ilya40umov.observability.logging

import kotlinx.coroutines.delay
import me.ilya40umov.observability.Greeting
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class LoggingCoRouterV1 {

    private val logger = LoggerFactory.getLogger(LoggingCoRouterV1::class.java)

    @Bean
    fun helloV2Route() = coRouter {
        GET("/v2/hello") {
            logger.info("hello() was called")
            delay(10L)
            logger.info("after delay()")
            ServerResponse.ok().bodyValueAndAwait(Greeting())
        }
    }

}