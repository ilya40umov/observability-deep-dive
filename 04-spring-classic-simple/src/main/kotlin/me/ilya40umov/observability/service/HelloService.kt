package me.ilya40umov.observability.service

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import me.ilya40umov.observability.model.Greeting
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Supplier

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
        return Observation.start("constructGreeting", observationRegistry).observe(Supplier {
            logger.info("Starting to construct the greeting...")
            Thread.sleep(10)
            Greeting(
                message = "Yo yo ${user.userId} from ${user.country}!"
            ).also {
                logger.info("Greeting constructed successfully")
            }
        })!!
    }
}