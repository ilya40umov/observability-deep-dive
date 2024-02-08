package me.ilya40umov.observability

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServiceAApplication

fun main(args: Array<String>) {
    runApplication<ServiceAApplication>(*args)
}
