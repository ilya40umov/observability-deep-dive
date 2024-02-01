package me.ilya40umov.observability.helper

import java.util.UUID

fun generateTraceId() = UUID.randomUUID().toString().substring(0, 13)