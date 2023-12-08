package me.ilya40umov.cowebfilter

import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloControllerV1 {

    private val logger = LoggerFactory.getLogger(HelloControllerV1::class.java)

    @GetMapping("/v1/hello")
    suspend fun hello(): String {
        logger.info("hello() was called, corrId: ${CorrIdHolder.corrId.get()}")
        delay(10)
        logger.info("after delay()")
        return "Hello!"
    }

}