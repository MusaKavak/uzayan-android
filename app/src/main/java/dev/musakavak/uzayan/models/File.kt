package dev.musakavak.uzayan.models

data class File(
    val name: String,
    val extension: String?,
    val path: String,
    val isFile: Boolean,
    val isHidden: Boolean,
    val size: Long,
    var isRoot: Boolean,
    var parent: File?,
    var children: List<File>?
)
