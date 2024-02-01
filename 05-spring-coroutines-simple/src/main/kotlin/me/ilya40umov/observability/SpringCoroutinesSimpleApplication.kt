package me.ilya40umov.observability

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringCoroutinesSimpleApplication

fun main(args: Array<String>) {
    runApplication<SpringCoroutinesSimpleApplication>(*args)
}
