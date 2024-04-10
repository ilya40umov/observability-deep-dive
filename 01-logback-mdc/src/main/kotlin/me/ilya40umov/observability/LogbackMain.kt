package me.ilya40umov.observability

import kotlin.system.exitProcess

fun main() {
    val examples = listOf(
        "LogbackHelloWorldV1" to ::logbackHelloWorldV1,
        "LogbackMdcClassicV1" to ::logbackMdcClassicV1,
        "LogbackMdcCoroutinesV1" to ::logbackMdcCoroutinesV1,
        "LogbackMdcReactorV1" to ::logbackMdcReactorV1,
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