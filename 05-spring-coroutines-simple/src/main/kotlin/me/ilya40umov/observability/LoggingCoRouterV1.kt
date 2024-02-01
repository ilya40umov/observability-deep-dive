@file:Suppress("DuplicatedCode")

package me.ilya40umov.observability

import kotlinx.coroutines.delay
import me.ilya40umov.observability.model.Greeting
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
    fun alohaV1Route() = coRouter {
        GET("/v1/aloha") {
            logger.info("aloha() was called")
            delay(10L)
            logger.info("after delay()")
            ServerResponse.ok().bodyValueAndAwait(Greeting())
        }
    }

}