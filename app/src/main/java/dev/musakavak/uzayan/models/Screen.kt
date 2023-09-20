package dev.musakavak.uzayan.models

data class Screen(
    val name: String,
    val width: String,
    val height: String,
    val nameWithSizes: String = "$name (${width}x${height})",
)