package me.ilya40umov.observability.aspect

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.Job
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Method
import java.util.Arrays
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

@Aspect
@Component
class ObservedSuspendingAspect(
    private val observationRegistry: ObservationRegistry
) {
    internal class DelegatingContinuation<T>(
        override val context: CoroutineContext,
        private val underlying: Continuation<T>
    ) : Continuation<T> {
        override fun resumeWith(result: Result<T>) {
            underlying.resumeWith(result)
        }
    }

    @Around("execution (@me.ilya40umov.observability.aspect.ObservedSuspending * *.*(..))")
    fun observeMethod(pjp: ProceedingJoinPoint): Any? {
        val method: Method = getMethod(pjp)
        val observed: ObservedSuspending = method.getAnnotation(ObservedSuspending::class.java)
        return observe(pjp, method, observed)
    }

    private fun observe(pjp: ProceedingJoinPoint, method: Method, observed: ObservedSuspending): Any? {
        val observation: Observation = Observation.createNotStarted(observed.name, observationRegistry)
        return if (Continuation::class.java.isAssignableFrom(method.parameterTypes.last())) {
            observation.start()
            try {
                val continuation = pjp.args.last() as Continuation<*>
                val coroutineContext = continuation.context.minusKey(Job.Key)

                val newCoroutineContext =
                    coroutineContext + observation.openScope().use { observationRegistry.asContextElement() }

                val newArgs = Arrays.copyOf(pjp.args, pjp.args.size)
                newArgs[newArgs.size - 1] = DelegatingContinuation(newCoroutineContext, continuation)

                (pjp.proceed(newArgs) as Mono<*>).doOnError { error ->
                    observation.error(error)
                }.doOnTerminate {
                    observation.stop()
                }
            } catch (error: Throwable) {
                observation.error(error)
                observation.stop()
                throw error
            }
        } else {
            observation.observeChecked<Any, Throwable> { pjp.proceed() }
        }
    }

    private fun getMethod(pjp: ProceedingJoinPoint): Method {
        val method: Method = (pjp.signature as MethodSignature).method
        if (method.getAnnotation(ObservedSuspending::class.java) == null) {
            return pjp.target.javaClass.getMethod(method.name, *method.parameterTypes)
        }
        return method
    }
}