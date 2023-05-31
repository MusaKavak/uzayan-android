package dev.musakavak.uzayan.models

data class AllowList(
    var file: Boolean = false,
    var renameFile: Boolean = false,
    var deleteFile: Boolean = false,
    var sendFile: Boolean = false,
    var receiveFile: Boolean = false,

    var mediaSession: Boolean = false,
    var mediaSessionControl: Boolean = false,

    var notifications: Boolean = false,
    var notificationControls: Boolean = false,
)
