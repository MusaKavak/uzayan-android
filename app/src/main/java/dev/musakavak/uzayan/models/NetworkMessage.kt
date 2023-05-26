package dev.musakavak.uzayan.models

data class NetworkMessage<T>(
    val event: String,
    val payload: T
)
