package me.ilya40umov.observability

import brave.baggage.BaggageField
import brave.propagation.CurrentTraceContext
import brave.propagation.TraceContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.ilya40umov.observability.helper.TracingFactory
import me.ilya40umov.observability.model.UserData
import org.slf4j.LoggerFactory
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

private const val BRAVE_COROUTINES_V2 = "BraveCoroutinesV2"
private val countryBaggage = BaggageField.create("country")
private val userIdBaggage = BaggageField.create("userId")
private val tracing = TracingFactory.tracing(countryBaggage, userIdBaggage)
private val tracer = tracing.tracer()
private val logger = LoggerFactory.getLogger(BRAVE_COROUTINES_V2)

// see https://github.com/openzipkin/brave/issues/820
private class TracingContextElement(
    private val currentTraceContext: CurrentTraceContext,
    private val initial: TraceContext = currentTraceContext.get()
) : ThreadContextElement<CurrentTraceContext.Scope>, AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<TracingContextElement>

    override fun updateThreadContext(context: CoroutineContext): CurrentTraceContext.Scope {
        return currentTraceContext.maybeScope(initial)
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: CurrentTraceContext.Scope) {
        oldState.close()
    }
}

fun main() = runBlocking {
    logger.info("Entering main()")

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val jobs = listOf(
        UserData(userId = "Dwalin", country = "Erebor"),
        UserData(userId = "Fili", country = "Erebor"),
        UserData(userId = "Kili", country = "Erebor"),
        UserData(userId = "Bandobras", country = "The Shire"),
        UserData(userId = "Posco", country = "The Shire"),
        UserData(userId = "Merry", country = "The Shire")
    ).map { (userId, country) ->
        val trace = tracer.newTrace()
            .name(BRAVE_COROUTINES_V2)
            .tag("userId", userId)
            .tag("country", country)
            .start()
        userIdBaggage.updateValue(trace.context(), userId)
        countryBaggage.updateValue(trace.context(), country)

        tracer.withSpanInScope(trace).use {
            coroutineScope.launch(
                // see https://github.com/openzipkin/brave/issues/820 for more info on this solution
                TracingContextElement(tracing.currentTraceContext())
            ) {
                logger.info("Processing - Phase 1 (before delay).")
                delay(10)
                logger.info("Processing - Phase 1 (after delay).")
                launch {
                    logger.info("Processing - Phase 2.")
                    delay(10)
                    trace.finish()
                }
            }
        }
    }

    jobs.forEach { it.join() }

    logger.info("Leaving main()")
}