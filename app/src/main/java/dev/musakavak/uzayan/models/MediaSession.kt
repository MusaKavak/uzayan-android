package dev.musakavak.uzayan.models

data class MediaSession(
    val albumArt: String?,
    val artist: String?,
    val albumName: String?,
    val duration: Long?,
    val isPlaying: Boolean,
    val packageName: String?,
    val position: Long?,
    val ratingType: String?,
    val title: String?,
    val token: String
)
