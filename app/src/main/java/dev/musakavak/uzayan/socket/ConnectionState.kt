package dev.musakavak.uzayan.socket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.musakavak.uzayan.models.Screen

class ConnectionState {
    companion object {
        var currentStatus by mutableStateOf(200)
        var connectedClientName by mutableStateOf<String?>(null)
        var isConnectionSecure by mutableStateOf<Boolean?>(null)
        var connectingStatus by mutableStateOf<Int?>(null)
        var remoteCommands by mutableStateOf<List<String>?>(null)
        var screens by mutableStateOf<List<Screen>?>(null)

        fun setDefault(){
            currentStatus = 200
            connectedClientName = null
            isConnectionSecure = null
            connectingStatus = null
            remoteCommands = null
            screens = null
        }
    }
}