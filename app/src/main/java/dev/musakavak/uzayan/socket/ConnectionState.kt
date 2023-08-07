package dev.musakavak.uzayan.socket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ConnectionState {
    companion object {
        var connectedClientName by mutableStateOf<String?>(null)
        var isConnectionSecure by mutableStateOf<Boolean?>(null)
    }
}