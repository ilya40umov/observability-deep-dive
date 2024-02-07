package me.ilya40umov.observability

import io.micrometer.tracing.BaggageInScope
import io.micrometer.tracing.Tracer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.ilya40umov.observability.service.HelloService
import me.ilya40umov.observability.model.Greeting
import me.ilya40umov.observability.model.UserData
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@RestController
class HelloControllerV3(
    private val helloService: HelloService
) {

    @GetMapping("/v3/hello")
    fun hello(@RequestAttribute("user") user: UserData): Greeting {
        return helloService.sayHelloTo(user)
    }

    @EnableWebMvc
    @Configuration
    class WebMvcConfig(
        private val tracer: Tracer
    ) : WebMvcConfigurer {
        override fun addInterceptors(registry: InterceptorRegistry) {
            registry.addInterceptor(UserAddingInterceptor(tracer))
                .addPathPatterns("/v3/hello")
        }
    }

    class UserAddingInterceptor(
        private val tracer: Tracer
    ) : HandlerInterceptor {
        override fun preHandle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            handler: Any
        ): Boolean {
            val user = UserData(userId = "Legolas", country = "Mirkwood")
            val baggage = listOf(
                tracer.createBaggageInScope("userId", user.userId),
                tracer.createBaggageInScope("country", user.country)
            )
            request.setAttribute("user", user)
            request.setAttribute("extra_baggage", baggage)
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