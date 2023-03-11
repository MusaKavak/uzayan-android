package dev.musakavak.uzayan.models

data class File(
    val name:String?,
    val path: String?,
    val isFile: Boolean?,
    val isHidden: Boolean?,
    var parent: File?,
    var children: List<File>?
)
