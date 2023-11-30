package me.ilya40umov.observability.helpers

import brave.Tracing
import brave.baggage.BaggageField
import brave.baggage.BaggagePropagation
import brave.baggage.BaggagePropagationConfig
import brave.baggage.CorrelationScopeConfig
import brave.context.slf4j.MDCScopeDecorator
import brave.propagation.B3Propagation
import brave.propagation.ThreadLocalCurrentTraceContext
import brave.sampler.Sampler

object TracingFactory {
    fun tracing(vararg baggageFields: BaggageField) =
        Tracing.newBuilder()
            .sampler(Sampler.NEVER_SAMPLE)
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