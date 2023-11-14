package me.ilya40umov.observability

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks

@SpringBootApplication
class ObservabilityApplication

fun main(args: Array<String>) {
    // Q: why is this needed?
    Hooks.enableAutomaticContextPropagation();
    runApplication<ObservabilityApplication>(*args)
}
