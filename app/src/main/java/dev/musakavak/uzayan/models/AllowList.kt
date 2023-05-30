package dev.musakavak.uzayan.models

data class AllowList(
    var mediaSessions: Boolean = false,
    var notifications: Boolean = false,
    var notificationTransfer: Boolean = false,
    var fileTransfer: Boolean = false,
    var imageTransfer: Boolean = false,
)
