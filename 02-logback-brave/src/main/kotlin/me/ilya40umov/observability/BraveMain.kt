package me.ilya40umov.observability

import kotlin.system.exitProcess

fun main() {
    val examples = listOf(
        "BraveHelloWorldV1" to ::braveHelloWorldV1,
        "BraveExecutorsV1" to ::braveExecutorsV1,
        "BraveExecutorsV2" to ::braveExecutorsV2,
        "BraveCoroutinesV1" to ::braveCoroutinesV1,
        "BraveCoroutinesV2" to ::braveCoroutinesV2,
        "BraveReactorV1" to ::braveReactorV1,
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
        examples[line.toInt()].second()
        exitProcess(0)
    }
}