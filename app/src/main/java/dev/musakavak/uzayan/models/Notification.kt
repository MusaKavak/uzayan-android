package dev.musakavak.uzayan.models

data class Notification(
    val infoText: String?,
    val key: String?,
    val largeIcon: String?,
    val packageName: String?,
    val title: String?,
    val text: String?,
    val bigText: String?,
    val actions: List<String>?,
    val progressMax: Int?,
    val process: Int?,
    val isGroup: Boolean?,
)
