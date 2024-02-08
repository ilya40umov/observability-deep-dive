package me.ilya40umov.observability

import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
class UserOrdersControllerV1 {
    private val logger = LoggerFactory.getLogger(UserOrdersControllerV1::class.java)

    @GetMapping("/v1/orders/{country}/{userId}")
    suspend fun getMyOrders(
        @PathVariable userId: String,
        @PathVariable country: String
    ): String {
        logger.info("Requesting orders from database.")
        delay(10)
        if (Random.nextInt(10) == 0) {
            throw RuntimeException("Failed to load orders from DB.")
        }
        logger.info("Retrieved orders from Service B.")
        return "Your orders are: Empire State Building, Yacht Koru, 150 Satellites, 1 Cheeseburger"
    }
}