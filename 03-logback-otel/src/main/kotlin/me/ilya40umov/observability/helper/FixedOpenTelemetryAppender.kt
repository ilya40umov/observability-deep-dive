package me.ilya40umov.observability.helper

import io.opentelemetry.instrumentation.logback.mdc.v1_0.OpenTelemetryAppender

class FixedOpenTelemetryAppender : OpenTelemetryAppender() {

    // XXX the original implementation does not stop the attached appenders
    override fun stop() {
        super.detachAndStopAllAppenders()
        super.stop()
    }
}