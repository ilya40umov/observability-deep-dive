package me.ilya40umov.observability

import io.micrometer.tracing.Tracer
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchangeOrNull

@RestController
class MyOrdersControllerV1(
    private val tracer: Tracer,
    webClientBuilder: WebClient.Builder
) {
    private val logger = LoggerFactory.getLogger(MyOrdersControllerV1::class.java)
    private val webClient = webClientBuilder.baseUrl("http://localhost:8081").build()

    @GetMapping("/v1/my/orders")
    suspend fun getMyOrders(): String {
        val (userId, country) = "Jeff Bezos" to "US"
        return withBaggage(userId, country) {
            logger.info("Requesting orders from Service B.")
            val orders = webClient.get().uri(
                "/v1/orders/{country}/{userId}",
                mapOf("userId" to userId, "country" to country)
            ).awaitExchangeOrNull { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.awaitBody<String>()
                } else {
                    throw RuntimeException("Failed to fetch orders from B!")
                }
            } ?: "user not found"
            logger.info("Retrieved orders from Service B.")
            orders
        }
    }

    private suspend fun <T> withBaggage(userId: String, country: String, block: suspend () -> T): T {
        return tracer.createBaggageInScope("country", country).use {
            tracer.createBaggageInScope("userId", userId).use {
                block()
            }
        }
    }
}