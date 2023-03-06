package dev.musakavak.uzayan.socket

import com.google.gson.Gson
import dev.musakavak.uzayan.models.ConnectionObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.net.Socket

class Client {

    companion object {
        private var client: Socket? = null
        private var writer: PrintWriter? = null
        private val gson = Gson()
        private val scope = CoroutineScope(Dispatchers.IO)

        fun initializeClient(address: String, port: Int, sendPairRequest: Boolean, code: String?) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    val newClient = Socket(address, port)
                    writer = PrintWriter(newClient.getOutputStream())
                    client = newClient
                    if (sendPairRequest && code != null) {
                        emit("Pair", code)
                    }
                }
            }
        }

        fun <T> emit(event: String, payload: T) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    val json = gson.toJson(ConnectionObject(event, payload))
                    client?.let {
                        if (it.isConnected) {
                            writer?.println(json)
                            writer?.flush()
                        }
                    }
                }
            }
        }
    }

}