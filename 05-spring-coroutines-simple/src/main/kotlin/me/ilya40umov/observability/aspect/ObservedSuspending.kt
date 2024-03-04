package me.ilya40umov.observability.aspect

// XXX @Observed currently can't be used on suspending methods
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ObservedSuspending(val name: String)
