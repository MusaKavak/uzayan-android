package dev.musakavak.uzayan.models

data class MediaSession(
    val albumArt: String?,
    val artist: String?,
    val albumName: String?,
    val duration: Long?,
    val packageName: String?,
    val title: String?,
    val token: Int?
)
