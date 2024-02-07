package me.ilya40umov.observability

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import io.micrometer.tracing.Tracer
import kotlinx.coroutines.withContext
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

const val USER_ATTRIBUTE = "user"

@Component
class UserAddingFilter(
    private val observationRegistry: ObservationRegistry,
    private val tracer: Tracer
) : CoWebFilter() {

    private val logger = LoggerFactory.getLogger(UserAddingFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val path = exchange.request.path.pathWithinApplication().value()
        if (!path.matches(".*/(aloha|hello)".toRegex())) {
            return chain.filter(exchange)
        }

        val user = when {
            path.contains("/v1/hello") -> UserData(userId = "Ned Stark", country = "Seven Kingdoms")
            else -> UserData(userId = "Daemon Blackfyre", country = "Free Cities")
        }

        exchange.attributes[USER_ATTRIBUTE] = user

        // XXX we could call tracer.createBaggageInScope() before going into withContext(..),
        //  but then once we are out of withContext(..) the tracing context seems to be lost
        withContext(observationRegistry.asContextElement()) {
            tracer.createBaggageInScope("country", user.country).use {
                tracer.createBaggageInScope("userId", user.userId).use {
                    logger.info("Before chain.filter()")
                    chain.filter(exchange)
                    logger.info("After chain.filter()")
                }
            }
        }
    }
}