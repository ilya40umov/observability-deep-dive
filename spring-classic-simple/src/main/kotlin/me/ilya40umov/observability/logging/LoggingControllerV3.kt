package me.ilya40umov.observability.logging

import io.micrometer.tracing.BaggageInScope
import io.micrometer.tracing.Tracer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.ilya40umov.observability.Greeting
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@RestController
class LoggingControllerV3(
    private val loggingService: LoggingService
) {

    @GetMapping("/v3/hello")
    fun hello(): Greeting {
        loggingService.method1()
        return Greeting()
    }

    @EnableWebMvc
    @Configuration
    class WebMvcConfig(
        private val tracer: Tracer
    ) : WebMvcConfigurer {
        override fun addInterceptors(registry: InterceptorRegistry) {
            registry.addInterceptor(BaggageAddingInterceptor(tracer))
                .addPathPatterns("/v3/hello")
        }
    }

    class BaggageAddingInterceptor(
        private val tracer: Tracer
    ) : HandlerInterceptor {
        override fun preHandle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            handler: Any
        ): Boolean {
            request.setAttribute(
                "extra_baggage",
                listOf(
                    tracer.createBaggageInScope("userId", "Legolas"),
                    tracer.createBaggageInScope("country", "Mirkwood")
                )
            )
            return true
        }

        @Suppress("UNCHECKED_CAST")
        override fun afterCompletion(
            request: HttpServletRequest,
            response: HttpServletResponse,
            handler: Any,
            ex: Exception?
        ) {
            val baggage = request.getAttribute("extra_baggage") as List<BaggageInScope>
            baggage.forEach(BaggageInScope::close)
        }
    }
}