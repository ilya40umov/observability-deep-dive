package me.ilya40umov.cowebfilter

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.withContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
@Order(49)
class ObservationFilter(
    private val observationRegistry: ObservationRegistry
) : CoWebFilter() {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        withContext(observationRegistry.asContextElement()) {
            chain.filter(exchange)
        }
    }
}