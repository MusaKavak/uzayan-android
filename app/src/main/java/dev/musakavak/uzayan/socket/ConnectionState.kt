package dev.musakavak.uzayan.socket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ConnectionState {
    companion object {
        var currentStatus by mutableStateOf(200)
        var connectedClientName by mutableStateOf<String?>(null)
        var isConnectionSecure by mutableStateOf<Boolean?>(null)
        var connectingStatus by mutableStateOf<Int?>(null)
    }
}