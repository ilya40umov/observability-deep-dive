package me.ilya40umov.observability.service

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import me.ilya40umov.observability.model.Greeting
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Service
class HelloService(
    private val observationRegistry: ObservationRegistry
) {

    private val logger = LoggerFactory.getLogger(HelloService::class.java)

    suspend fun sayHelloTo(user: UserData): Greeting {
        logger.info("Entering sayHelloTo()")
        return try {
            constructGreetingFor(user)
        } finally {
            logger.info("Leaving sayHelloTo()")
        }
    }

    private suspend fun constructGreetingFor(user: UserData): Greeting {
        return Observation.createNotStarted("constructGreeting", observationRegistry).observeAndWait {
            logger.info("Starting to construct the greeting...")

            delay(10L)
            logger.info("after delay()")

            Mono.delay(Duration.ofMillis(10)).awaitSingleOrNull()
            logger.info("after Mono.delay()")

            val delayedExecutor = CompletableFuture.delayedExecutor(10L, TimeUnit.MILLISECONDS)
            CompletableFuture.supplyAsync({ "dummy value" }, delayedExecutor).await()
            logger.info("after CompletableFuture.await()")

            Greeting(
                message = "Greetings to ${user.userId} of ${user.country}!"
            ).also {
                logger.info("Greeting constructed successfully")
            }
        }
    }

    // Observation API is not very Kotlin-friendly at the moment
    private suspend fun <T> Observation.observeAndWait(block: suspend () -> T): T {
        start()
        return withContext(
            openScope().use { observationRegistry.asContextElement() }
        ) {
            try {
                block()
            } catch (error: Throwable) {
                error(error)
                throw error
            } finally {
                stop()
            }
        }
    }
}