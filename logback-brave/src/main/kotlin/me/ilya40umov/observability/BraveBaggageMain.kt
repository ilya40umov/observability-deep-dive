package me.ilya40umov.observability

import brave.Tracing
import brave.baggage.BaggageField
import brave.baggage.BaggagePropagation
import brave.baggage.BaggagePropagationConfig
import brave.baggage.CorrelationScopeConfig
import brave.context.slf4j.MDCScopeDecorator
import brave.propagation.B3Propagation
import brave.propagation.ThreadLocalCurrentTraceContext
import brave.sampler.Sampler
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val countryBaggage = BaggageField.create("country")
private val userIdBaggage = BaggageField.create("userId")

private val tracing =
    Tracing.newBuilder()
        .sampler(Sampler.NEVER_SAMPLE)
        .currentTraceContext(
            ThreadLocalCurrentTraceContext
                .newBuilder()
                .addScopeDecorator(
                    MDCScopeDecorator.newBuilder()
                        .add(CorrelationScopeConfig.SingleCorrelationField.create(countryBaggage))
                        .add(CorrelationScopeConfig.SingleCorrelationField.create(userIdBaggage))
                        .build()
                )
                .build()
        )
        .propagationFactory(
            BaggagePropagation
                .newFactoryBuilder(B3Propagation.FACTORY)
                .add(BaggagePropagationConfig.SingleBaggageField.local(countryBaggage))
                .add(BaggagePropagationConfig.SingleBaggageField.local(userIdBaggage))
                .build()
        )
        .build()

private val logger = LoggerFactory.getLogger("BraveBaggageMain")

data class UserData(
    val userId: String,
    val country: String
)

fun main() {
    logger.info("Entering main()")
    try {
        val pool1 = Executors.newFixedThreadPool(2)
        val pool2 = Executors.newFixedThreadPool(4)

        listOf(
            UserData(userId = "Incognito1", country = "Inner Space"),
            UserData(userId = "Incognito2", country = "Inner Space"),
            UserData(userId = "Incognito3", country = "Inner Space"),
            UserData(userId = "Stitch1", country = "Outer Space"),
            UserData(userId = "Stitch2", country = "Outer Space"),
            UserData(userId = "Stitch3", country = "Outer Space")
        ).forEach { (userId, country) ->
            pool1.submit {
                val trace = tracing.tracer().newTrace()
                userIdBaggage.updateValue(trace.context(), userId)
                countryBaggage.updateValue(trace.context(), country)
                tracing.tracer().withSpanInScope(trace).use {
                    logger.info("Processing - Phase 1.")
                }
                Thread.sleep(10L)
                pool2.submit {
                    tracing.tracer().withSpanInScope(trace).use {
                        logger.info("Processing - Phase 2.")
                    }
                    trace.finish()
                }
                Thread.sleep(10L)
            }
        }
        pool1.shutdown()
        pool1.awaitTermination(1, TimeUnit.MINUTES)

        pool2.shutdown()
        pool2.awaitTermination(1, TimeUnit.MINUTES)
    } finally {
        logger.info("Leaving main()")
    }
}
