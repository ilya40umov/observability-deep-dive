package me.ilya40umov.cowebfilter.logging

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.withContext
import me.ilya40umov.cowebfilter.ImprovedCoWebFilter
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
@Order(49)
class ObservationFilter(
    private val observationRegistry: ObservationRegistry
) : ImprovedCoWebFilter() {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        withContext(observationRegistry.asContextElement()) {
            chain.filter(exchange)
        }
    }
}