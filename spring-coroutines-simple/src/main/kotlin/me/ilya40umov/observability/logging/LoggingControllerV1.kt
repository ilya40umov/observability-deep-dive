package me.ilya40umov.observability.logging

import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingleOrNull
import me.ilya40umov.observability.Greeting
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RestController
class LoggingControllerV1 {

    private val logger = LoggerFactory.getLogger(LoggingControllerV1::class.java)

    @GetMapping("/v1/hello")
    suspend fun hello(): Greeting {
        // XXX prior to Spring Boot 3.2.0 it was necessary to wrap this into `observationRegistry.asContextElement()`,
        //  but since Spring Boot 3.2.0 it can now be done in CoWebFilter
        logger.info("hello() was called")
        method1()
        return Greeting()
    }

    private suspend fun method1() {
        logger.info("method1() was called")
        delay(10L)
        logger.info("after delay()")
        Mono.delay(Duration.ofMillis(10)).awaitSingleOrNull()
        logger.info("after Mono.delay()")
        val delayedExecutor = CompletableFuture.delayedExecutor(10L, TimeUnit.MILLISECONDS)
        CompletableFuture.supplyAsync({ "dummy value" }, delayedExecutor).await()
        logger.info("after CompletableFuture.await()")
    }
}