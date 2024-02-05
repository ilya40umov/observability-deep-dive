package me.ilya40umov.observability.helper

import brave.Tracing
import brave.baggage.BaggageField
import brave.baggage.BaggagePropagation
import brave.baggage.BaggagePropagationConfig
import brave.baggage.CorrelationScopeConfig
import brave.context.slf4j.MDCScopeDecorator
import brave.propagation.B3Propagation
import brave.propagation.ThreadLocalCurrentTraceContext
import brave.sampler.Sampler
import zipkin2.reporter.brave.AsyncZipkinSpanHandler
import zipkin2.reporter.urlconnection.URLConnectionSender

object TracingFactory {
    fun tracing(vararg baggageFields: BaggageField): Tracing {
        val sender = URLConnectionSender.create("http://localhost:9411/api/v2/spans")
        val zipkinSpanHandler = AsyncZipkinSpanHandler.create(sender)
        Runtime.getRuntime().addShutdownHook(
            Thread {
                zipkinSpanHandler.flush()
                zipkinSpanHandler.close()
            }
        )
        return Tracing.newBuilder()
            .localServiceName("02-logback-brave")
            .sampler(Sampler.ALWAYS_SAMPLE)
            .addSpanHandler(zipkinSpanHandler)
            .currentTraceContext(
                ThreadLocalCurrentTraceContext
                    .newBuilder()
                    .addScopeDecorator(
                        MDCScopeDecorator.newBuilder()
                            .apply {
                                baggageFields.forEach {
                                    add(CorrelationScopeConfig.SingleCorrelationField.create(it))
                                }
                            }
                            .build()
                    )
                    .build()
            )
            .propagationFactory(
                BaggagePropagation
                    .newFactoryBuilder(B3Propagation.FACTORY)
                    .apply {
                        baggageFields.forEach {
                            add(BaggagePropagationConfig.SingleBaggageField.local(it))
                        }
                    }
                    .build()
            )
            .build()
    }
}