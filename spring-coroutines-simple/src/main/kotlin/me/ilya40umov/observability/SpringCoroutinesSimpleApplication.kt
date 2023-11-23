package me.ilya40umov.observability

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks

@SpringBootApplication
class SpringCoroutinesSimpleApplication

fun main(args: Array<String>) {
    Hooks.enableAutomaticContextPropagation()
    runApplication<SpringCoroutinesSimpleApplication>(*args)
}
