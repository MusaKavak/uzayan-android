package dev.musakavak.uzayan.models

data class AllowList(
    val mediaSessions: Boolean = false,
    val notifications: Boolean = false,
    val notificationTransfer: Boolean = false,
    val fileTransfer: Boolean = false,
    val imageTransfer: Boolean = false,
)
