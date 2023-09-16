package dev.musakavak.uzayan.socket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ConnectionState {
    companion object {
        var currentStatus by mutableStateOf(202)
        var connectedClientName by mutableStateOf<String?>(null)
        var isConnectionSecure by mutableStateOf<Boolean?>(true)
        var connectingStatus by mutableStateOf<Int?>(null)
        var remoteCommands by mutableStateOf<List<String>?>(null)
    }
}