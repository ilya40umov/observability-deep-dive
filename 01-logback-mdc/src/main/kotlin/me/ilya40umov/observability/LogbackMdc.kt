package me.ilya40umov.observability

fun main() {
    println("Select example:")
    println("1. LogbackHelloWorldV1")
    println("2. LogbackMdcClassicV1")
    println("3. LogbackMdcCoroutinesV1")
    println("4. LogbackMdcReactorV1")
    print("> ")
    while (true) {
        val line = readlnOrNull()
        if (line.isNullOrBlank() || line.toIntOrNull() !in 1..4) {
            print("> ")
            continue
        }
        when (line.toInt()) {
            1 -> logbackHelloWorldV1()
            2 -> logbackMdcClassicV1()
            3 -> logbackMdcCoroutinesV1()
            4 -> logbackMdcReactorV1()
        }
    }
}