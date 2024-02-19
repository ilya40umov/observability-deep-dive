package me.ilya40umov.observability.service

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import me.ilya40umov.observability.model.Greeting
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HelloService(
    private val observationRegistry: ObservationRegistry
) {

    private val logger = LoggerFactory.getLogger(HelloService::class.java)

    fun sayHelloTo(user: UserData): Greeting {
        logger.info("Entering sayHelloTo()")
        return try {
            constructGreetingFor(user)
        } finally {
            logger.info("Leaving sayHelloTo()")
        }
    }

    private fun constructGreetingFor(user: UserData): Greeting {
        return Observation.createNotStarted("constructGreeting", observationRegistry).observeNotNull {
            logger.info("Starting to construct the greeting...")
            Thread.sleep(10)
            Greeting(
                message = "Yo yo ${user.userId} from ${user.country}!"
            ).also {
                logger.info("Greeting constructed successfully")
            }
        }
    }

    // Observation API is not very Kotlin-friendly at the moment
    fun <T : Any> Observation.observeNotNull(block: () -> T): T = observeBlock(block)

    private fun <T> Observation.observeBlock(block: () -> T): T {
        start()
        return try {
            openScope().use {
                block()
            }
        } catch (error: Throwable) {
            error(error)
            throw error
        } finally {
            stop()
        }
    }
}