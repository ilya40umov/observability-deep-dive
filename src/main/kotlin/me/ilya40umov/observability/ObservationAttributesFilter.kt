package me.ilya40umov.observability

import io.micrometer.observation.ObservationRegistry
import io.micrometer.tracing.Tracer
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
class ObservationAttributesFilter(
    private val observationRegistry: ObservationRegistry,
    private val tracer: Tracer
) : CoWebFilter() {
    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        // Q: why doesn't this work?
        observationRegistry.currentObservation?.highCardinalityKeyValue("userId", "Aragon")
        // ... while using tracer directly does work ...
        tracer.createBaggageInScope("country", "Middle Earth").use {
            chain.filter(exchange)
        }
    }
}