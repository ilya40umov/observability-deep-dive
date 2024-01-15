package me.ilya40umov.cowebfilter

import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.withContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
@Order(50)
class CorrIdFilter : CoWebFilter() {
    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        withContext(CorrIdHolder.corrId.asContextElement("42")) {
            chain.filter(exchange)
        }
    }
}