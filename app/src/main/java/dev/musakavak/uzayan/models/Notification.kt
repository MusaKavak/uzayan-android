package dev.musakavak.uzayan.models

data class Notification(
    val key: String,
    val packageName: String,
    val title: String?,
    val text: String?,
    val bigText: String?,
    val infoText: String?,
    val largeIcon: String?,
    val actions: List<String>?,
    val isGroup: Boolean,
    val progressMax: Int?,
    val process: Int?,
)
