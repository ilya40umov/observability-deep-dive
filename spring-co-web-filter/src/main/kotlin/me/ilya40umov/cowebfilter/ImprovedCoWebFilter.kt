package me.ilya40umov.cowebfilter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import kotlin.coroutines.CoroutineContext

abstract class ImprovedCoWebFilter : WebFilter {

    final override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // this is likely needed to support chaining multiple filters
        val ctx = exchange.attributes[CoWebFilter.COROUTINE_CONTEXT_ATTRIBUTE] as CoroutineContext?
        return mono(ctx ?: Dispatchers.Unconfined) {
            filter(exchange, object : CoWebFilterChain {
                override suspend fun filter(exchange: ServerWebExchange) {
                    exchange.attributes[CoWebFilter.COROUTINE_CONTEXT_ATTRIBUTE] =
                        currentCoroutineContext().minusKey(Job)
                    chain.filter(exchange).awaitSingleOrNull()
                }
            })
        }.then()
    }

    abstract suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain)

    companion object {
        const val COROUTINE_CONTEXT_ATTRIBUTE = "org.springframework.web.server.CoWebFilter.context"
    }
}

interface CoWebFilterChain {
    suspend fun filter(exchange: ServerWebExchange)
}