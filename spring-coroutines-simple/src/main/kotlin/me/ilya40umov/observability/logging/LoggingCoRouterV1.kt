@file:Suppress("DuplicatedCode")

package me.ilya40umov.observability.logging

import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.delay
import me.ilya40umov.observability.Greeting
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.server.CoWebFilter
import kotlin.coroutines.CoroutineContext

@Configuration
class LoggingCoRouterV1 {

    private val logger = LoggerFactory.getLogger(LoggingCoRouterV1::class.java)

    @Bean
    fun helloV2Route() = coRouter {
        // XXX this is needed as the coroutine context from CoWebFilter is otherwise not propagated
        context { it.exchange().attributes[CoWebFilter.COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext }
        GET("/v2/hello") {
            logger.info("hello() was called")
            delay(10L)
            logger.info("after delay()")
            ServerResponse.ok().bodyValueAndAwait(Greeting())
        }
    }

}