package me.ilya40umov.observability

import kotlin.system.exitProcess

fun main() {
    val examples = listOf(
        "OtelHelloWorldV1" to ::otelHelloWorldV1,
        "OtelExecutorsV1" to ::otelExecutorsV1,
        "OtelCoroutinesV1" to ::otelCoroutinesV1,
        "OtelReactorV1" to ::otelReactorV1,
    )

    println("Select example:")
    examples.forEachIndexed { index, example ->
        println("${index + 1}. ${example.first}")
    }
    print("> ")
    while (true) {
        val line = readlnOrNull()
        if (line.isNullOrBlank() || line.toIntOrNull() !in examples.indices.map { it + 1 }) {
            print("> ")
            continue
        }
        examples[line.toInt() - 1].second()
        exitProcess(0)
    }
}