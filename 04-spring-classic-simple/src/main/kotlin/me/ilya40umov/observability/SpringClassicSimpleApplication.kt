package me.ilya40umov.observability

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringClassicSimpleApplication

fun main(args: Array<String>) {
    runApplication<SpringClassicSimpleApplication>(*args)
}
