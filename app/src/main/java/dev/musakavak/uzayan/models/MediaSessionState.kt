package dev.musakavak.uzayan.models

data class MediaSessionState(
    val token: String?,
    val isActive: Boolean?,
    val duration: Long?,
    val position: Long?,
    val isRated: Boolean?,
    val hasHeart: Boolean?,
    val isThumbsUp: Boolean?,
)
