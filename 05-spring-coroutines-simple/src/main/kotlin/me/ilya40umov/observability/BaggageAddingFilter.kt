package me.ilya40umov.observability

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import io.micrometer.tracing.Tracer
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
class  BaggageAddingFilter(
    private val observationRegistry: ObservationRegistry,
    private val tracer: Tracer
) : CoWebFilter() {

    private val logger = LoggerFactory.getLogger(BaggageAddingFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val path = exchange.request.path.pathWithinApplication().value()
        if (!path.contains("v1")) {
            return chain.filter(exchange)
        }

        val (country, userId) = when {
            path.contains("/v1/hello") -> "Seven Kingdoms" to "Ned Stark"
            else -> "Free Cities" to "Daemon Blackfyre"
        }

        // XXX we could call tracer.createBaggageInScope() before going into withContext(..),
        //  but then once we are out of withContext(..) the tracing context seems to be lost
        withContext(observationRegistry.asContextElement()) {
            tracer.createBaggageInScope("country", country).use {
                tracer.createBaggageInScope("userId", userId).use {
                    logger.info("Before chain.filter()")
                    chain.filter(exchange)
                    logger.info("After chain.filter()")
                }
            }
        }
    }
}