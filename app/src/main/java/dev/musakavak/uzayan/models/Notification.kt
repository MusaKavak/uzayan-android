package dev.musakavak.uzayan.models

data class Notification(
    val key: String,
    val groupKey: String,
    val title: String? = null,
    val text: String? = null,
    val bigText: String? = null,
    val infoText: String? = null,
    val largeIcon: String? = null,
    val smallIcon: String? = null,
    val actions: List<String>? = null,
    val progressMax: Int? = null,
    val progress: Int? = null,
)
