package dev.musakavak.uzayan.models

data class ConnectionObject<T>(
    val message: String,
    val input: T
)
