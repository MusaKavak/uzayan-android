package dev.musakavak.uzayan.models

data class EmitObject<T>(
    val message: String,
    val emitObject: T
)
