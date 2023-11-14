package me.ilya40umov.observability

import io.micrometer.common.KeyValues
import org.springframework.http.server.reactive.observation.DefaultServerRequestObservationConvention
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext
import org.springframework.stereotype.Component

@Component
class ServerRequestObservationConvention : DefaultServerRequestObservationConvention() {
    override fun getHighCardinalityKeyValues(context: ServerRequestObservationContext): KeyValues {
        return super.getHighCardinalityKeyValues(context)
            // XXX why doesn't this work?
            .and("userId", "Gandalf")
    }
}