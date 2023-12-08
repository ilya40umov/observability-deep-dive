package me.ilya40umov.observability.logging

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
class BaggageAddingFilter(
    private val observationRegistry: ObservationRegistry,
    private val tracer: Tracer
) : CoWebFilter() {

    private val logger = LoggerFactory.getLogger(BaggageAddingFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        if (!exchange.request.path.pathWithinApplication().value().contains("hello")) {
            return chain.filter(exchange)
        }

        withContext(observationRegistry.asContextElement()) {
            // XXX outside of this block {tracer.currentTraceContext().context()} is null
            //  so setting baggage has to happen within this block
            tracer.createBaggageInScope("country", "Middle Earth").use {
                tracer.createBaggageInScope("userId", "Saruman").use {
                    logger.info("Before chain.filter()")
                    chain.filter(exchange)
                    logger.info("After chain.filter()")
                }
            }
        }
    }
}