package me.ilya40umov.observability.helper

import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk

val openTelemetry: OpenTelemetrySdk by lazy {
    // XXX normally these values would be provided via environment variables
    System.getProperties().apply {
        this["otel.service.name"] = "03-logback-otel"
        this["otel.metrics.exporter"] = "none"
        this["otel.traces.exporter"] = "otlp"
        this["otel.exporter.otlp.traces.endpoint"] = "http://localhost:4317/"
        this["otel.logs.exporter"] = "none"
    }
    AutoConfiguredOpenTelemetrySdk.initialize().openTelemetrySdk.also { sdk ->
        Runtime.getRuntime().addShutdownHook(Thread {
            sdk.sdkLoggerProvider.forceFlush()
            sdk.shutdown()
        })
    }
}