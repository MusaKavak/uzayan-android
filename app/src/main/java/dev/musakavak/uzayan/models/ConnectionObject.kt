package dev.musakavak.uzayan.models

data class ConnectionObject<T>(
    val event: String,
    val input: T
)
